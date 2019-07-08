package org.apache.ibatis.builder.annotation;

import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.PropertyParser;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * 修改行: 54~72 (注: 该版本为MyBatis3.4.6)
 * 实现功能: BaseDao中的Provider注解上指定的Provider类, 优先指向BaseDao子接口的内部Provider类
 * 例: BaseDao有这样一行注解代码: @DeleteProvider(type = Provider.class, method = "deleteBatch")
 * 这里的type = Provider.class指向的是内部类BaseDao.Provider.class
 * 但是如果BaseDao的子接口TestDao也有一个内部类, 则这里的type = Provider.class优先指向这个内部类
 * 这样做的目的: 任何Java注解里不能有变量, 但是不同的Dao子接口, 其表名和字段名等一定是不同的(即变量), 这样做就间接赋予了Java注解里可以有变量的功能
 *
 * @author paul paul@gmail.com
 * @since 2019/3/28 20:04
 */
public class ProviderSqlSource implements SqlSource {

    private final Configuration configuration;

    private final SqlSourceBuilder sqlSourceParser;

    private final Class<?> providerType;

    private Method providerMethod;

    private String[] providerMethodArgumentNames;

    private Class<?>[] providerMethodParameterTypes;

    private ProviderContext providerContext;

    private Integer providerContextIndex;

    /**
     * @deprecated Please use the {@link #ProviderSqlSource(Configuration, Object, Class, Method)}
     * instead of this.
     */
    @Deprecated
    public ProviderSqlSource(Configuration configuration, Object provider) {

        this(configuration, provider, null, null);
    }

    /**
     * @since 3.4.5
     */
    public ProviderSqlSource(Configuration configuration, Object provider, Class<?> mapperType,
        Method mapperMethod) {

        String providerMethodName;
        try {
            this.configuration = configuration;
            this.sqlSourceParser = new SqlSourceBuilder(configuration);
            // begin
            // 注解上指定的provider类
            Class<?> providerType =
                (Class<?>) provider.getClass().getMethod("type").invoke(provider);
            // 获得此类的外部类, 该方法与getEnclosingClass效果类型, 区别在于: getEnclosingClass对匿名内部类也有效
            Class<?> declaringClass = providerType.getDeclaringClass();
            //  是否一下三个条件都满足:
            // 1. 存在外部类/接口
            // 2. 外部类与mapperType(实际的DAO接口, 即BaseDao的子接口)不是同一个类/接口
            // 3. 外部类是mapperType的父类/接口(isAssignableFrom无法判断出到底是父类/接口还是本身)
            if (declaringClass != null && !declaringClass.getName().equals(mapperType.getName()) &&
                declaringClass.isAssignableFrom(mapperType)) {
                Class<?>[] declaredClasses = mapperType.getDeclaredClasses();
                // 如果存在内部类
                if (declaredClasses.length > 0) {
                    // 获得mapperType的内部类, 这里规定, mapperType只能有一个内部类, 且这个类就是providerType
                    providerType = mapperType.getDeclaredClasses()[0];
                }
            }
            this.providerType = providerType;
            // begin
            providerMethodName = (String) provider.getClass().getMethod("method").invoke(provider);

            for (Method m : this.providerType.getMethods()) {
                if (providerMethodName.equals(m.getName()) &&
                    CharSequence.class.isAssignableFrom(m.getReturnType())) {
                    if (providerMethod != null) {
                        throw new BuilderException(
                            "Error creating SqlSource for SqlProvider. Method '"
                            + providerMethodName + "' is found multiple in SqlProvider '" +
                            this.providerType.getName()
                            + "'. Sql provider method can not overload.");
                    }
                    this.providerMethod = m;
                    this.providerMethodArgumentNames =
                        new ParamNameResolver(configuration, m).getNames();
                    this.providerMethodParameterTypes = m.getParameterTypes();
                }
            }
        } catch (BuilderException e) {
            throw e;
        } catch (Exception e) {
            throw new BuilderException("Error creating SqlSource for SqlProvider.  Cause: " + e, e);
        }
        if (this.providerMethod == null) {
            throw new BuilderException("Error creating SqlSource for SqlProvider. Method '"
                                       + providerMethodName + "' not found in SqlProvider '" +
                                       this.providerType.getName() + "'.");
        }
        for (int i = 0; i < this.providerMethodParameterTypes.length; i++) {
            Class<?> parameterType = this.providerMethodParameterTypes[i];
            if (parameterType == ProviderContext.class) {
                if (this.providerContext != null) {
                    throw new BuilderException(
                        "Error creating SqlSource for SqlProvider. ProviderContext found multiple" +
                        " in SqlProvider method ("
                        + this.providerType.getName() + "." + providerMethod.getName()
                        +
                        "). ProviderContext can not define multiple in SqlProvider method " +
                        "argument.");
                }
                this.providerContext = new ProviderContext(mapperType, mapperMethod);
                this.providerContextIndex = i;
            }
        }
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {

        SqlSource sqlSource = createSqlSource(parameterObject);
        return sqlSource.getBoundSql(parameterObject);
    }

    private SqlSource createSqlSource(Object parameterObject) {

        try {
            int bindParameterCount =
                providerMethodParameterTypes.length - (providerContext == null ? 0 : 1);
            String sql;
            if (providerMethodParameterTypes.length == 0) {
                sql = invokeProviderMethod();
            } else if (bindParameterCount == 0) {
                sql = invokeProviderMethod(providerContext);
            } else if (bindParameterCount == 1 &&
                       (parameterObject == null || providerMethodParameterTypes[
                           (providerContextIndex == null || providerContextIndex == 1) ? 0 : 1]
                           .isAssignableFrom(parameterObject.getClass()))) {
                sql = invokeProviderMethod(extractProviderMethodArguments(parameterObject));
            } else if (parameterObject instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> params = (Map<String, Object>) parameterObject;
                sql = invokeProviderMethod(
                    extractProviderMethodArguments(params, providerMethodArgumentNames));
            } else {
                throw new BuilderException("Error invoking SqlProvider method ("
                                           + providerType.getName() + "." + providerMethod.getName()
                                           + "). Cannot invoke a method that holds "
                                           + (bindParameterCount == 1 ? "named argument(@Param)" :
                    "multiple arguments")
                                           +
                                           " using a specifying parameterObject. In this case, " +
                                           "please specify a 'java.util.Map' object.");
            }
            Class<?> parameterType =
                parameterObject == null ? Object.class : parameterObject.getClass();
            return sqlSourceParser.parse(replacePlaceholder(sql), parameterType, new HashMap<>(0));
        } catch (BuilderException e) {
            throw e;
        } catch (Exception e) {
            throw new BuilderException("Error invoking SqlProvider method ("
                                       + providerType.getName() + "." + providerMethod.getName()
                                       + ").  Cause: " + e, e);
        }
    }

    private Object[] extractProviderMethodArguments(Object parameterObject) {

        if (providerContext != null) {
            Object[] args = new Object[2];
            args[providerContextIndex == 0 ? 1 : 0] = parameterObject;
            args[providerContextIndex] = providerContext;
            return args;
        } else {
            return new Object[]{parameterObject};
        }
    }

    private Object[] extractProviderMethodArguments(Map<String, Object> params,
        String[] argumentNames) {

        Object[] args = new Object[argumentNames.length];
        for (int i = 0; i < args.length; i++) {
            if (providerContextIndex != null && providerContextIndex == i) {
                args[i] = providerContext;
            } else {
                args[i] = params.get(argumentNames[i]);
            }
        }
        return args;
    }

    private String invokeProviderMethod(Object... args) throws Exception {

        Object targetObject = null;
        if (!Modifier.isStatic(providerMethod.getModifiers())) {
            targetObject = providerType.newInstance();
        }
        CharSequence sql = (CharSequence) providerMethod.invoke(targetObject, args);
        return sql != null ? sql.toString() : null;
    }

    private String replacePlaceholder(String sql) {

        return PropertyParser.parse(sql, configuration.getVariables());
    }

}