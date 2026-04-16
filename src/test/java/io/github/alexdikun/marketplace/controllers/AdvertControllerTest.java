package io.github.alexdikun.marketplace.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import io.github.alexdikun.marketplace.entities.CategoryEntity;
import io.github.alexdikun.marketplace.entities.UserEntity;
import io.github.alexdikun.marketplace.request.AdvertRequest;
import io.github.alexdikun.marketplace.response.AdvertResponse;
import io.github.alexdikun.marketplace.service.AdvertService;
import io.github.alexdikun.marketplace.utils.TestFactoryData;

@WebMvcTest(AdvertController.class)
public class AdvertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdvertService advertService;

    @Test
    void createAdvert_ShouldCreateSuccessfully() throws Exception {
        CategoryEntity categoryEntity = TestFactoryData.createCategory(null);

        AdvertRequest advertRequest = TestFactoryData.createAdvertRequest(categoryEntity);
        AdvertResponse advertResponse = TestFactoryData.createAdvertResponse();

        when(advertService.createAdvert(request)).thenReturn(response);

        mockMvc.perform(post("/api/adverts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestFactoryData.asJsonString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(advertResponse.getId()))
            .andExpect(jsonPath("$.title").value(advertResponse.getTitle()))
            .andExpect(jsonPath("$.price").value(advertResponse.getPrice()));

        verify(advertService).createAdvert(advertRequest);
    }

    @Test
    void createAdvert_ShouldReturn400WhenValidationFails() throws Exception {
        // given
        AdvertRequest invalidRequest = new AdvertRequest();
        invalidRequest.setTitle(""); // пустое название — нарушаем валидацию

        // when & then
        mockMvc.perform(post("/api/adverts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestFactoryData.asJsonString(invalidRequest)))
            .andExpect(status().isBadRequest());

        verify(advertService, never()).createAdvert(any());
    }

    @Test
    void searchAdverts_ShouldReturnResults() throws Exception {
        // given
        String query = "laptop";
        int page = 0;
        int size = 10;

        Page<AdvertResponse> pageResponse = TestFactoryData.createPageOfAdvertResponses();

        when(advertService.searchAdverts(query, page, size)).thenReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/api/adverts/search")
                .param("query", query)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isNotEmpty())
            .andExpect(jsonPath("$.totalElements").value(pageResponse.getTotalElements()));

        verify(advertService, times(1)).searchAdverts(query, page, size);
    }

    @Test
    void getAdvert_ShouldReturnAdvert() throws Exception {
        // given
        Long id = 1L;
        AdvertResponse response = TestFactoryData.createAdvertResponse();
        response.setId(id);

        when(advertService.getAdvert(id)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/adverts/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.title").value(response.getTitle()));

        verify(advertService, times(1)).getAdvert(id);
    }

    @Test
    void getAdvert_ShouldReturn404WhenNotFound() throws Exception {
        // given
        Long nonExistentId = 999L;

        when(advertService.getAdvert(nonExistentId))
            .thenThrow(new NotFoundException("Объявление не найдено"));

        // when & then
        mockMvc.perform(get("/api/adverts/{id}", nonExistentId))
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("Объявление не найдено")));

        verify(advertService, times(1)).getAdvert(nonExistentId);
    }
}
