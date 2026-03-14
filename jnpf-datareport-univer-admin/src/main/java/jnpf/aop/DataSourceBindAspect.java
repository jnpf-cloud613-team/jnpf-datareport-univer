package jnpf.aop;

import cn.dev33.satoken.context.SaHolder;
import jnpf.base.UserInfo;
import jnpf.config.ConfigValueUtil;
import jnpf.database.util.NotTenantPluginHolder;
import jnpf.database.util.TenantDataSourceUtil;
import jnpf.util.StringUtil;
import jnpf.util.TenantHolder;
import jnpf.util.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 *
 * @author JNPF开发平台组
 * @version V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date 2021/3/15 17:12
 */
@Slf4j
@Aspect
@Component
@Order(1)
public class DataSourceBindAspect {

    @Autowired
    private ConfigValueUtil configValueUtil;

    @Pointcut("(execution(* jnpf.*.controller.*.*(..)) || execution(* jnpf.controller.*.*(..))))")
    public void bindDataSource() {

    }

    /**
     * NoDataSourceBind 不需要绑定数据库的注解
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("bindDataSource() && !@annotation(jnpf.util.NoDataSourceBind)")
    public Object doAroundService(ProceedingJoinPoint pjp) throws Throwable {
        if (configValueUtil.isMultiTenancy()) {
            if(StringUtil.isEmpty(TenantHolder.getDatasourceId())){
                UserInfo userInfo = UserProvider.getUser();
                String url = null;
                try{
                    url = SaHolder.getRequest().getRequestPath();
                }catch (Exception ee){ }
                log.error("租户" + userInfo.getTenantId() + "数据库不存在, URL: {}, TOKEN: {}", url, userInfo.getToken());
                return null;
            }
            return pjp.proceed();
        }
        Object obj = pjp.proceed();
        return obj;
    }



    @Around("bindDataSource() && @annotation(jnpf.util.NoDataSourceBind)")
    public Object doAroundService2(ProceedingJoinPoint pjp) throws Throwable {
        try{
            NotTenantPluginHolder.setNotSwitchAlwaysFlag();
            //Filter中提前设置租户信息, 不需要切库的方法进行清除切库
            TenantDataSourceUtil.clearLocalTenantInfo();
            return pjp.proceed();
        }finally {
            NotTenantPluginHolder.clearNotSwitchAlwaysFlag();
        }
    }
}
