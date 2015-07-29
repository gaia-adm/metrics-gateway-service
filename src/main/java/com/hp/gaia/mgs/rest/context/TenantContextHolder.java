package com.hp.gaia.mgs.rest.context;

/**
 * Created by belozovs on 7/29/2015.
 * Keep tenant id for the given thread
 */
public class TenantContextHolder {

    private final ThreadLocal<String> tenantIdLocal = new ThreadLocal<>();

    private static TenantContextHolder ourInstance = new TenantContextHolder();

    public static TenantContextHolder getInstance() {
        return ourInstance;
    }

    private TenantContextHolder() {
    }

    public String getTenantIdLocal() {
        return tenantIdLocal.get();
    }

    public void setTenantIdLocal(String tenantId){
        tenantIdLocal.set(tenantId);
    }
}
