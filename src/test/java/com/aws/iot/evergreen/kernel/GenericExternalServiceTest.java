package com.aws.iot.evergreen.kernel;

import com.aws.iot.evergreen.config.Topic;
import com.aws.iot.evergreen.testcommons.testutilities.EGServiceTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;

import static com.aws.iot.evergreen.kernel.EvergreenService.SERVICE_LIFECYCLE_NAMESPACE_TOPIC;
import static com.aws.iot.evergreen.kernel.Lifecycle.LIFECYCLE_BOOTSTRAP_NAMESPACE_TOPIC;
import static com.aws.iot.evergreen.kernel.Lifecycle.LIFECYCLE_INSTALL_NAMESPACE_TOPIC;
import static com.aws.iot.evergreen.packagemanager.KernelConfigResolver.VERSION_CONFIG_KEY;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;

public class GenericExternalServiceTest extends EGServiceTestUtil {
    private GenericExternalService ges;

    @BeforeEach
    void beforeEach() {
        lenient().doReturn(Topic.of(context, VERSION_CONFIG_KEY, "1.0.0")).when(config).find(eq(VERSION_CONFIG_KEY));
        ges = new GenericExternalService(initializeMockedConfig());
    }

    @Test
    void GIVEN_new_config_without_bootstrap_WHEN_isBootstrapRequired_THEN_return_false() {
        assertFalse(ges.isBootstrapRequired(Collections.emptyMap()));
        assertFalse(ges.isBootstrapRequired(new HashMap<String, Object>() {{
            put(SERVICE_LIFECYCLE_NAMESPACE_TOPIC, new HashMap<String, Object>() {{
                put(LIFECYCLE_INSTALL_NAMESPACE_TOPIC, "echo done");
            }});
        }}));
        assertFalse(ges.isBootstrapRequired(new HashMap<String, Object>() {{
            put(SERVICE_LIFECYCLE_NAMESPACE_TOPIC, new HashMap<String, Object>() {{
                put(LIFECYCLE_BOOTSTRAP_NAMESPACE_TOPIC, null);
            }});
        }}));
    }

    @Test
    void GIVEN_new_config_with_new_version_WHEN_isBootstrapRequired_THEN_return_true() {
        assertTrue(ges.isBootstrapRequired(new HashMap<String, Object>() {{
            put(VERSION_CONFIG_KEY, "1.0.1");
            put(SERVICE_LIFECYCLE_NAMESPACE_TOPIC, new HashMap<String, Object>() {{
                put(LIFECYCLE_BOOTSTRAP_NAMESPACE_TOPIC, "echo done");
            }});
        }}));
    }

    @Test
    void GIVEN_new_config_with_new_bootstrap_definition_WHEN_isBootstrapRequired_THEN_return_true() {
        doReturn(Topic.of(context, LIFECYCLE_BOOTSTRAP_NAMESPACE_TOPIC, "echo complete")).when(config)
                .findNode(eq(SERVICE_LIFECYCLE_NAMESPACE_TOPIC), eq(LIFECYCLE_BOOTSTRAP_NAMESPACE_TOPIC));

        assertTrue(ges.isBootstrapRequired(new HashMap<String, Object>() {{
            put(VERSION_CONFIG_KEY, "1.0.0");
            put(SERVICE_LIFECYCLE_NAMESPACE_TOPIC, new HashMap<String, Object>() {{
                put(LIFECYCLE_BOOTSTRAP_NAMESPACE_TOPIC, "echo done");
            }});
        }}));
    }
}