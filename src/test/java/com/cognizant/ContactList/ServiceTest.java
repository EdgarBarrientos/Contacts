package com.cognizant.ContactList;

import com.cognizant.ContactList.DTO.ContactDTO;
import com.cognizant.ContactList.Domains.ContactList;
import com.cognizant.ContactList.Repositories.ContactListRepository;
import com.cognizant.ContactList.Services.ContactListService;
import com.cognizant.ContactList.Specifications.ContactSpecification;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @Mock
    private ContactListRepository repo;

    @InjectMocks
    private ContactListService contactService;

    @Test
    public void Test_AllContacts() throws Exception {
        List<ContactList> listOfContacts = new ContactsSetup().getContactLists();
        when(repo.findAll()).thenReturn(listOfContacts);
        List<ContactDTO> listOfDTOContacts = new ContactsSetup().getContactDTOLists();

        List<ContactDTO> actualList = contactService.findAllContacts(Optional.empty(),Optional.empty());
        assertEquals(listOfDTOContacts, actualList);
    }

    @Test
    public void getContactsWithSpecTest() throws Exception{
        List<ContactList> listOfContacts = new ContactsSetup().getContactLists();
        List<ContactDTO> listOfDTOContacts = new ContactsSetup().getContactDTOLists();
        List<ContactDTO> expected =listOfDTOContacts.stream()
                        .filter(c -> c.getSurName().contains("Brown"))
                        .collect(Collectors.toList());
        when(repo.findAll()).thenReturn(listOfContacts);

        List<ContactDTO> actualListOfDTOContacts=contactService.findAllContacts(Optional.empty(),Optional.of("Brown"));
        assertEquals(expected, actualListOfDTOContacts);
    }


    @Test
    public void testSaveContact() throws Exception{
        String contactJson = new ContactsSetup().getJSON("/Contact.json");
        ContactList contactList = new ObjectMapper().readValue(contactJson,ContactList.class);

        String contactSaveJson = new ContactsSetup().getJSON("/ContactSave.json");
        ContactList saveContact = new ObjectMapper().readValue(contactSaveJson,ContactList.class);

        when(repo.save(contactList)).thenReturn(saveContact);

        ContactDTO expectedDTO = contactService.transformModelToDTO(saveContact);
        ContactDTO actualDTO = contactService.save(contactList);

        assertEquals(expectedDTO, actualDTO);
    }





}
