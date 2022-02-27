package cz.cvut.kbss.ear.copyto.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import cz.cvut.kbss.ear.copyto.dao.Generator;
import cz.cvut.kbss.ear.copyto.dto.OrderDTO;
import cz.cvut.kbss.ear.copyto.model.Category;
import cz.cvut.kbss.ear.copyto.model.Order;
import cz.cvut.kbss.ear.copyto.rest.handler.ErrorInfo;
import cz.cvut.kbss.ear.copyto.service.CategoryService;
import cz.cvut.kbss.ear.copyto.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CategoryRestControllerTest extends BaseControllerTestRunner {

    @Mock
    private CategoryService categoryServiceMock;

    @Mock
    private OrderService orderServiceMock;

    @InjectMocks
    private CategoryController sut;

    @BeforeEach
    public void setUp() {
        super.setUp(sut);
    }

    @Test
    public void getAllReturnsCategoriesReadByCategoryService() throws Exception {
        final List<Category> categories = IntStream.range(0, 5).mapToObj(i -> {
            final Category cat = new Category();
            cat.setName("Category" + i);
            cat.setId(Generator.randomInt());
            return cat;
        }).collect(Collectors.toList());
        when(categoryServiceMock.findCategories()).thenReturn(categories);

        final MvcResult mvcResult = mockMvc.perform(get("/rest/categories")).andReturn();
        final List<Category> result = readValue(mvcResult, new TypeReference<List<Category>>() {
        });
        assertEquals(categories.size(), result.size());
        verify(categoryServiceMock).findCategories();
    }

    @Test
    public void createCategoryCreatesCategoryUsingService() throws Exception {
        final Category toCreate = new Category();
        toCreate.setName("New Category");

        System.out.println("++++++++++++++++++++++++++++++" + (toJson(toCreate)));

        mockMvc.perform(post("/rest/categories").content(toJson(toCreate)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
        final ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryServiceMock).createCategory(captor.capture());
        assertEquals(toCreate.getName(), captor.getValue().getName());
    }

    @Test
    public void createCategoryReturnsResponseWithLocationHeader() throws Exception {
        final Category toCreate = new Category();
        toCreate.setName("New Category");
        toCreate.setId(Generator.randomInt());

        final MvcResult mvcResult = mockMvc
                .perform(post("/rest/categories").content(toJson(toCreate)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated()).andReturn();
        verifyLocationEquals("/rest/categories/id/" + toCreate.getId(), mvcResult);
    }

    @Test
    public void getByIdReturnsMatchingCategory() throws Exception {
        final Category category = new Category();
        category.setId(Generator.randomInt());
        category.setName("category");
        when(categoryServiceMock.findCategory(category.getId())).thenReturn(category);
        final MvcResult mvcResult = mockMvc.perform(get("/rest/categories/id/" + category.getId())).andReturn();

        final Category result = readValue(mvcResult, Category.class);
        assertNotNull(result);
        assertEquals(category.getId(), result.getId());
        assertEquals(category.getName(), result.getName());
    }
    @Test
    public void getByIdThrowsNotFoundForUnknownCategoryId() throws Exception {
        final int id = 123;
        final MvcResult mvcResult = mockMvc.perform(get("/rest/categories/id/" + id)).andExpect(status().isNotFound())
                .andReturn();
        final ErrorInfo result = readValue(mvcResult, ErrorInfo.class);
        assertNotNull(result);
        assertThat(result.getMessage(), containsString("Category identified by "));
        assertThat(result.getMessage(), containsString(Integer.toString(id)));
    }

    @Test
    public void getProductsByCategoryThrowsNotFoundForUnknownCategoryId() throws Exception {
        final int id = 123;
        mockMvc.perform(get("/rest/categories/id/" + id + "/orders")).andExpect(status().isNotFound());
        verify(categoryServiceMock).findCategory(id);
        verify(orderServiceMock, never()).findOrders(any());
    }

    @Test
    public void addProductToCategoryThrowsNotFoundForUnknownCategory() throws Exception {
        final Order product = Generator.generateOrder();
        product.setId(Generator.randomInt());
        final int categoryId = 123;
        mockMvc.perform(put("/rest/categories/id/" + categoryId + "/order/" + product.getId()).content(toJson(product)).contentType(
                MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNotFound());
        //verify(categoryServiceMock).findCategory(categoryId);
        verify(categoryServiceMock, never()).addOrder(any(), any());
    }

    @Test
    public void removeProductRemovesProductFromCategory() throws Exception {
        final Category category = new Category();
        category.setName("test");
        category.setId(Generator.randomInt());
        when(categoryServiceMock.findCategory((Integer) any())).thenReturn(category);
        final Order order = Generator.generateOrder();
        order.setId(Generator.randomInt());
        order.setCategory(category);
        when(orderServiceMock.findOrder(any())).thenReturn(order);
        mockMvc.perform(delete("/rest/categories/id/" + category.getId() + "/orders/id/" + order.getId()))
                .andExpect(status().isNoContent());
        verify(categoryServiceMock).removeOrder(category, order);
    }

    @Test
    public void removeProductThrowsNotFoundForUnknownProductId() throws Exception {
        final Category category = new Category();
        category.setName("test");
        category.setId(Generator.randomInt());
        when(categoryServiceMock.findCategory((Integer) any())).thenReturn(category);
        final int unknownId = 123;
        mockMvc.perform(delete("/rest/categories/id/" + category.getId() + "/orders/id/" + unknownId))
                .andExpect(status().isNotFound());
        verify(categoryServiceMock).findCategory(category.getId());
        verify(categoryServiceMock, never()).removeOrder(any(), any());
    }
}
