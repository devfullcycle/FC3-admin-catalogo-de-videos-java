package com.fullcycle.admin.catalogo.infrastructure.api;

import com.fullcycle.admin.catalogo.ControllerTest;
import com.fullcycle.admin.catalogo.application.category.create.CreateCategoryUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@ControllerTest(controllers = CategoryAPI.class)
public class CategoryAPITest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CreateCategoryUseCase createCategoryUseCase;

    @Test
    public void test() {

    }
}
