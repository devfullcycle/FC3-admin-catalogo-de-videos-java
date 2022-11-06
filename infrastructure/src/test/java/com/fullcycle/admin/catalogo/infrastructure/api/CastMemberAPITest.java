package com.fullcycle.admin.catalogo.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalogo.ControllerTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.application.castmember.create.CreateCastMemberOutput;
import com.fullcycle.admin.catalogo.application.castmember.create.DefaultCreateCastMemberUseCase;
import com.fullcycle.admin.catalogo.application.castmember.delete.DefaultDeleteCastMemberUseCase;
import com.fullcycle.admin.catalogo.application.castmember.retrieve.get.CastMemberOutput;
import com.fullcycle.admin.catalogo.application.castmember.retrieve.get.DefaultGetCastMemberByIdUseCase;
import com.fullcycle.admin.catalogo.application.castmember.retrieve.list.CastMemberListOutput;
import com.fullcycle.admin.catalogo.application.castmember.retrieve.list.DefaultListCastMembersUseCase;
import com.fullcycle.admin.catalogo.application.castmember.update.DefaultUpdateCastMemberUseCase;
import com.fullcycle.admin.catalogo.application.castmember.update.UpdateCastMemberOutput;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.CreateCastMemberRequest;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.UpdateCastMemberRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = CastMemberAPI.class)
public class CastMemberAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private DefaultCreateCastMemberUseCase createCastMemberUseCase;

    @MockBean
    private DefaultDeleteCastMemberUseCase deleteCastMemberUseCase;

    @MockBean
    private DefaultGetCastMemberByIdUseCase getCastMemberByIdUseCase;

    @MockBean
    private DefaultListCastMembersUseCase listCastMembersUseCase;

    @MockBean
    private DefaultUpdateCastMemberUseCase updateCastMemberUseCase;

    @Test
    public void givenAValidCommand_whenCallsCreateCastMember_shouldReturnItsIdentifier() throws Exception {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();
        final var expectedId = CastMemberID.from("o1i2u3i1o");

        final var aCommand =
                new CreateCastMemberRequest(expectedName, expectedType);

        when(createCastMemberUseCase.execute(any()))
                .thenReturn(CreateCastMemberOutput.from(expectedId));

        // when
        final var aRequest = post("/cast_members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand));

        final var response = this.mvc.perform(aRequest)
                .andDo(print());

        // then
        response.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/cast_members/" + expectedId.getValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

        verify(createCastMemberUseCase).execute(argThat(actualCmd ->
                Objects.equals(expectedName, actualCmd.name())
                        && Objects.equals(expectedType, actualCmd.type())
        ));
    }

    @Test
    public void givenAnInvalidName_whenCallsCreateCastMember_shouldReturnNotification() throws Exception {
        // given
        final String expectedName = null;
        final var expectedType = Fixture.CastMembers.type();

        final var expectedErrorMessage = "'name' should not be null";

        final var aCommand =
                new CreateCastMemberRequest(expectedName, expectedType);

        when(createCastMemberUseCase.execute(any()))
                .thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        // when
        final var aRequest = post("/cast_members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand));

        final var response = this.mvc.perform(aRequest)
                .andDo(print());

        // then
        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(createCastMemberUseCase).execute(argThat(actualCmd ->
                Objects.equals(expectedName, actualCmd.name())
                        && Objects.equals(expectedType, actualCmd.type())
        ));
    }

    @Test
    public void givenAValidId_whenCallsGetById_shouldReturnIt() throws Exception {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();

        final var aMember = CastMember.newMember(expectedName, expectedType);
        final var expectedId = aMember.getId().getValue();

        when(getCastMemberByIdUseCase.execute(any()))
                .thenReturn(CastMemberOutput.from(aMember));

        // when
        final var aRequest = get("/cast_members/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)))
                .andExpect(jsonPath("$.name", equalTo(expectedName)))
                .andExpect(jsonPath("$.type", equalTo(expectedType.name())))
                .andExpect(jsonPath("$.created_at", equalTo(aMember.getCreatedAt().toString())))
                .andExpect(jsonPath("$.updated_at", equalTo(aMember.getUpdatedAt().toString())));

        verify(getCastMemberByIdUseCase).execute(eq(expectedId));
    }

    @Test
    public void givenAInvalidId_whenCallsGetByIdAndCastMemberDoesntExists_shouldReturnNotFound() throws Exception {
        // given
        final var expectedErrorMessage = "CastMember with ID 123 was not found";
        final var expectedId = CastMemberID.from("123");

        when(getCastMemberByIdUseCase.execute(any()))
                .thenThrow(NotFoundException.with(CastMember.class, expectedId));

        // when
        final var aRequest = get("/cast_members/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

        verify(getCastMemberByIdUseCase).execute(eq(expectedId.getValue()));
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateCastMember_shouldReturnItsIdentifier() throws Exception {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();

        final var aMember = CastMember.newMember(expectedName, expectedType);
        final var expectedId = aMember.getId();

        final var aCommand =
                new UpdateCastMemberRequest(expectedName, expectedType);

        when(updateCastMemberUseCase.execute(any()))
                .thenReturn(UpdateCastMemberOutput.from(expectedId));

        // when
        final var aRequest = put("/cast_members/{id}", expectedId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand));

        final var response = this.mvc.perform(aRequest)
                .andDo(print());

        // then
        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

        verify(updateCastMemberUseCase).execute(argThat(actualCmd ->
                Objects.equals(expectedId.getValue(), actualCmd.id())
                        && Objects.equals(expectedName, actualCmd.name())
                        && Objects.equals(expectedType, actualCmd.type())
        ));
    }

    @Test
    public void givenAnInvalidName_whenCallsUpdateCastMember_shouldReturnNotification() throws Exception {
        // given
        final var aMember = CastMember.newMember("Vin Di", CastMemberType.DIRECTOR);
        final var expectedId = aMember.getId();

        final String expectedName = null;
        final var expectedType = Fixture.CastMembers.type();

        final var expectedErrorMessage = "'name' should not be null";

        final var aCommand =
                new UpdateCastMemberRequest(expectedName, expectedType);

        when(updateCastMemberUseCase.execute(any()))
                .thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        // when
        final var aRequest = put("/cast_members/{id}", expectedId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand));

        final var response = this.mvc.perform(aRequest)
                .andDo(print());

        // then
        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(updateCastMemberUseCase).execute(argThat(actualCmd ->
                Objects.equals(expectedId.getValue(), actualCmd.id())
                        && Objects.equals(expectedName, actualCmd.name())
                        && Objects.equals(expectedType, actualCmd.type())
        ));
    }

    @Test
    public void givenAnInvalidId_whenCallsUpdateCastMember_shouldReturnNotFound() throws Exception {
        // given
        final var expectedId = CastMemberID.from("123");

        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();

        final var expectedErrorMessage = "CastMember with ID 123 was not found";

        final var aCommand =
                new UpdateCastMemberRequest(expectedName, expectedType);

        when(updateCastMemberUseCase.execute(any()))
                .thenThrow(NotFoundException.with(CastMember.class, expectedId));

        // when
        final var aRequest = put("/cast_members/{id}", expectedId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand));

        final var response = this.mvc.perform(aRequest)
                .andDo(print());

        // then
        response.andExpect(status().isNotFound())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

        verify(updateCastMemberUseCase).execute(argThat(actualCmd ->
                Objects.equals(expectedId.getValue(), actualCmd.id())
                        && Objects.equals(expectedName, actualCmd.name())
                        && Objects.equals(expectedType, actualCmd.type())
        ));
    }

    @Test
    public void givenAValidId_whenCallsDeleteById_shouldDeleteIt() throws Exception {
        // given
        final var expectedId = "123";

        doNothing()
                .when(deleteCastMemberUseCase).execute(any());

        // when
        final var aRequest = delete("/cast_members/{id}", expectedId);

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isNoContent());

        verify(deleteCastMemberUseCase).execute(eq(expectedId));
    }

    @Test
    public void givenValidParams_whenCallListCastMembers_shouldReturnIt() throws Exception {
        // given
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());

        final var expectedPage = 1;
        final var expectedPerPage = 20;
        final var expectedTerms = "Alg";
        final var expectedSort = "type";
        final var expectedDirection = "desc";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(CastMemberListOutput.from(aMember));

        when(listCastMembersUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        // when
        final var aRequest = get("/cast_members")
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("search", expectedTerms)
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(aMember.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", equalTo(aMember.getName())))
                .andExpect(jsonPath("$.items[0].type", equalTo(aMember.getType().name())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(aMember.getCreatedAt().toString())));

        verify(listCastMembersUseCase).execute(argThat(aQuery ->
                Objects.equals(expectedPage, aQuery.page())
                        && Objects.equals(expectedPerPage, aQuery.perPage())
                        && Objects.equals(expectedTerms, aQuery.terms())
                        && Objects.equals(expectedSort, aQuery.sort())
                        && Objects.equals(expectedDirection, aQuery.direction())
        ));
    }

    @Test
    public void givenEmptyParams_whenCallListCastMembers_shouldUseDefaultsAndReturnIt() throws Exception {
        // given
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(CastMemberListOutput.from(aMember));

        when(listCastMembersUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        // when
        final var aRequest = get("/cast_members")
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(aMember.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", equalTo(aMember.getName())))
                .andExpect(jsonPath("$.items[0].type", equalTo(aMember.getType().name())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(aMember.getCreatedAt().toString())));

        verify(listCastMembersUseCase).execute(argThat(aQuery ->
                Objects.equals(expectedPage, aQuery.page())
                        && Objects.equals(expectedPerPage, aQuery.perPage())
                        && Objects.equals(expectedTerms, aQuery.terms())
                        && Objects.equals(expectedSort, aQuery.sort())
                        && Objects.equals(expectedDirection, aQuery.direction())
        ));
    }
}
