package com.jordanrobin.financial_erp.config;

import com.jordanrobin.financial_erp.domain.auth.privilege.PrivilegeRepository;
import com.jordanrobin.financial_erp.domain.auth.role.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RolePrivilegeInitializerTest {

    @Mock
    private PrivilegeRepository privilegeRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RolePrivilegeInitializer initializer; // Mockito injecte les deux repos ici

    @Test
    void testAllExceptLogic() {
        // Tes tests ici...
    }
}
