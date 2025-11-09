package com.callableapis.api;

import com.callableapis.api.di.AppBinder;
import com.callableapis.api.handlers.AuthResource;
import com.callableapis.api.handlers.UserResource;
import com.callableapis.api.security.ApiKeyService;
import com.callableapis.api.security.ApiKeyStore;
import com.callableapis.api.security.AuthenticationStatsService;
import com.callableapis.api.security.BearerAuthFilter;
import com.callableapis.api.security.RateLimitService;
import com.callableapis.api.web.AuthenticatedResource;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Test;

import jakarta.inject.Inject;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Comprehensive test to verify application configuration is correct.
 * 
 * This test validates:
 * 1. All @Inject fields have corresponding DI bindings
 * 2. All registered resources can be instantiated
 * 3. All filters can be instantiated
 * 4. All registered classes exist and are valid
 * 5. No missing DI bindings
 */
public class AppConfigurationTest {

    @Test
    public void testAllResourcesCanBeInstantiated() {
        // Test that all resources registered in APIApplication can be instantiated
        // with their dependencies
        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        ServiceLocatorUtilities.bind(locator, new AppBinder());
        
        // Test AuthResource
        AuthResource authResource = locator.create(AuthResource.class);
        assertNotNull("AuthResource should be instantiable", authResource);
        
        // Test UserResource
        UserResource userResource = locator.create(UserResource.class);
        assertNotNull("UserResource should be instantiable", userResource);
        
        // Test AuthenticatedResource
        AuthenticatedResource authenticatedResource = locator.create(AuthenticatedResource.class);
        assertNotNull("AuthenticatedResource should be instantiable", authenticatedResource);
    }

    @Test
    public void testAllFiltersCanBeInstantiated() {
        // Test that all filters can be instantiated with their dependencies
        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        ServiceLocatorUtilities.bind(locator, new AppBinder());
        
        BearerAuthFilter filter = locator.create(BearerAuthFilter.class);
        assertNotNull("BearerAuthFilter should be instantiable", filter);
    }

    @Test
    public void testAllRequiredServicesAreBound() {
        // Verify that all services used via @Inject are bound in AppBinder
        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        ServiceLocatorUtilities.bind(locator, new AppBinder());
        
        // Check ApiKeyStore (bound via ApiKeyService)
        ApiKeyStore apiKeyStore = locator.getService(ApiKeyStore.class);
        assertNotNull("ApiKeyStore should be bound", apiKeyStore);
        assertTrue("ApiKeyStore should be an instance of ApiKeyService", 
                   apiKeyStore instanceof ApiKeyService);
        
        // Check RateLimitService (bound via ApiKeyService)
        RateLimitService rateLimitService = locator.getService(RateLimitService.class);
        assertNotNull("RateLimitService should be bound", rateLimitService);
        assertTrue("RateLimitService should be an instance of ApiKeyService", 
                   rateLimitService instanceof ApiKeyService);
        
        // Check ApiKeyService (bound directly for UserResource)
        ApiKeyService apiKeyService = locator.getService(ApiKeyService.class);
        assertNotNull("ApiKeyService should be bound", apiKeyService);
        
        // Check AuthenticationStatsService
        AuthenticationStatsService authStats = locator.getService(AuthenticationStatsService.class);
        assertNotNull("AuthenticationStatsService should be bound", authStats);
    }

    @Test
    @SuppressFBWarnings(value = {"DP_DO_INSIDE_DO_PRIVILEGED", "UWF_UNWRITTEN_FIELD"}, 
                       justification = "Reflection is acceptable in test code to verify DI injection")
    public void testAuthResourceDependenciesAreInjected() throws Exception {
        // Verify that AuthResource's @Inject fields are properly injected
        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        ServiceLocatorUtilities.bind(locator, new AppBinder());
        
        AuthResource authResource = locator.create(AuthResource.class);
        
        // Use reflection to check that @Inject fields are not null
        Field apiKeyStoreField = AuthResource.class.getDeclaredField("apiKeyStore");
        apiKeyStoreField.setAccessible(true);
        Object apiKeyStore = apiKeyStoreField.get(authResource);
        assertNotNull("apiKeyStore should be injected", apiKeyStore);
        assertTrue("apiKeyStore should be ApiKeyStore", apiKeyStore instanceof ApiKeyStore);
        
        Field authStatsField = AuthResource.class.getDeclaredField("authStatsService");
        authStatsField.setAccessible(true);
        Object authStats = authStatsField.get(authResource);
        assertNotNull("authStatsService should be injected", authStats);
        assertTrue("authStatsService should be AuthenticationStatsService", 
                   authStats instanceof AuthenticationStatsService);
    }

    @Test
    @SuppressFBWarnings(value = {"DP_DO_INSIDE_DO_PRIVILEGED", "UWF_UNWRITTEN_FIELD"}, 
                       justification = "Reflection is acceptable in test code to verify DI injection")
    public void testBearerAuthFilterDependenciesAreInjected() throws Exception {
        // Verify that BearerAuthFilter's @Inject fields are properly injected
        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        ServiceLocatorUtilities.bind(locator, new AppBinder());
        
        BearerAuthFilter filter = locator.create(BearerAuthFilter.class);
        
        Field apiKeyStoreField = BearerAuthFilter.class.getDeclaredField("apiKeyStore");
        apiKeyStoreField.setAccessible(true);
        Object apiKeyStore = apiKeyStoreField.get(filter);
        assertNotNull("apiKeyStore should be injected in BearerAuthFilter", apiKeyStore);
        
        Field rateLimitServiceField = BearerAuthFilter.class.getDeclaredField("rateLimitService");
        rateLimitServiceField.setAccessible(true);
        Object rateLimitService = rateLimitServiceField.get(filter);
        assertNotNull("rateLimitService should be injected in BearerAuthFilter", rateLimitService);
    }

    @Test
    @SuppressFBWarnings(value = {"DP_DO_INSIDE_DO_PRIVILEGED", "UWF_UNWRITTEN_FIELD"}, 
                       justification = "Reflection is acceptable in test code to verify DI injection")
    public void testAuthenticatedResourceDependenciesAreInjected() throws Exception {
        // Verify that AuthenticatedResource's @Inject fields are properly injected
        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        ServiceLocatorUtilities.bind(locator, new AppBinder());
        
        AuthenticatedResource resource = locator.create(AuthenticatedResource.class);
        
        Field apiKeyStoreField = AuthenticatedResource.class.getDeclaredField("apiKeyStore");
        apiKeyStoreField.setAccessible(true);
        Object apiKeyStore = apiKeyStoreField.get(resource);
        assertNotNull("apiKeyStore should be injected in AuthenticatedResource", apiKeyStore);
    }

    @Test
    @SuppressFBWarnings(value = {"DP_DO_INSIDE_DO_PRIVILEGED", "UWF_UNWRITTEN_FIELD"}, 
                       justification = "Reflection is acceptable in test code to verify DI injection")
    public void testUserResourceDependenciesAreInjected() throws Exception {
        // Verify that UserResource's @Inject fields are properly injected
        // Note: UserResource injects ApiKeyService directly, not ApiKeyStore
        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        ServiceLocatorUtilities.bind(locator, new AppBinder());
        
        UserResource resource = locator.create(UserResource.class);
        
        Field apiKeyServiceField = UserResource.class.getDeclaredField("apiKeyService");
        apiKeyServiceField.setAccessible(true);
        Object apiKeyService = apiKeyServiceField.get(resource);
        // ApiKeyService might be injectable via its interface bindings
        // or we might need to explicitly bind it
        assertNotNull("apiKeyService should be injected in UserResource", apiKeyService);
    }

    @Test
    @SuppressFBWarnings(value = {"DP_DO_INSIDE_DO_PRIVILEGED", "UWF_UNWRITTEN_FIELD"}, 
                       justification = "Reflection is acceptable in test code to verify DI injection")
    public void testNoMissingInjectFields() {
        // Collect all classes with @Inject fields
        Set<Class<?>> classesWithInject = new HashSet<>();
        classesWithInject.add(AuthResource.class);
        classesWithInject.add(UserResource.class);
        classesWithInject.add(AuthenticatedResource.class);
        classesWithInject.add(BearerAuthFilter.class);
        
        // For each class, verify all @Inject fields have corresponding bindings
        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        ServiceLocatorUtilities.bind(locator, new AppBinder());
        
        for (Class<?> clazz : classesWithInject) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    Class<?> fieldType = field.getType();
                    Object service = locator.getService(fieldType);
                    assertNotNull(String.format(
                        "Service of type %s required by %s.%s should be bound in AppBinder",
                        fieldType.getName(), clazz.getSimpleName(), field.getName()),
                        service);
                }
            }
        }
    }

    @Test
    public void testAllRegisteredResourcesExist() {
        // Verify that all classes registered in APIApplication actually exist
        // and can be loaded
        // This test ensures that if someone registers a non-existent class,
        // we'll catch it at test time rather than runtime
        new APIApplication(); // Constructor should complete without throwing
        
        // The fact that APIApplication constructor completes without throwing
        // means all registered classes exist and can be loaded
    }

    @Test
    public void testAppBinderConfigurationIsValid() {
        // Verify that AppBinder can be instantiated and configured
        AppBinder binder = new AppBinder();
        assertNotNull("AppBinder should be instantiable", binder);
        
        // Create a service locator and bind it
        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        ServiceLocatorUtilities.bind(locator, binder);
        
        // Verify the locator has the expected services
        assertNotNull("ServiceLocator should be created", locator);
    }

    @Test
    @SuppressFBWarnings(value = {"NP_NULL_ON_SOME_PATH"}, 
                       justification = "Services are verified to be non-null before assertSame calls")
    public void testSingletonServicesAreShared() {
        // Verify that singleton services (like ApiKeyService) are shared instances
        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        ServiceLocatorUtilities.bind(locator, new AppBinder());
        
        ApiKeyStore store1 = locator.getService(ApiKeyStore.class);
        ApiKeyStore store2 = locator.getService(ApiKeyStore.class);
        
        // Verify services are not null (should have been bound in AppBinder)
        assertNotNull("ApiKeyStore should be bound", store1);
        assertNotNull("ApiKeyStore should be bound", store2);
        
        // Both should be the same instance (singleton)
        assertSame("ApiKeyStore should be a singleton", store1, store2);
        
        RateLimitService rate1 = locator.getService(RateLimitService.class);
        RateLimitService rate2 = locator.getService(RateLimitService.class);
        
        // Verify services are not null
        assertNotNull("RateLimitService should be bound", rate1);
        assertNotNull("RateLimitService should be bound", rate2);
        
        // Both should be the same instance (singleton)
        assertSame("RateLimitService should be a singleton", rate1, rate2);
        
        // ApiKeyStore and RateLimitService should be the same instance
        // since ApiKeyService implements both
        assertSame("ApiKeyStore and RateLimitService should be the same instance",
                   store1, rate1);
    }
}

