package com.canehealth.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.parser.StrictErrorHandler;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import static org.junit.Assert.*;

@SpringBootTest
@WebAppConfiguration
@RunWith(SpringRunner.class)
public class InjectorServiceTest {


    @Autowired
    InjectorService injectorService;

    Questionnaire questionnaire;

    @Before
    public void setUp() throws Exception {

        String sourceFile = "test-ques-1.json";
        final FhirContext ctx = FhirContext.forDstu3();
        ctx.setParserErrorHandler(new StrictErrorHandler());
        final IParser parser;
        if (sourceFile.toLowerCase().endsWith(".xml")) {
            parser = ctx.newXmlParser();
        } else {
            parser = ctx.newJsonParser();
        }
        parser.setPrettyPrint(true);

        IBaseResource resource;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(Paths.get(sourceFile).toString()).getInputStream(),
                        java.nio.charset.StandardCharsets.UTF_8))) {
            resource = parser.parseResource(reader);
            // beapen: If resource is a questionnaire, apply dataelement injector
            if (resource.getClass() == Questionnaire.class) {
                this.questionnaire = (Questionnaire) resource;
                //resource = injectorService.processQuestionnaire(questionnaire);
            }
        } catch (final IOException e) {
            throw new RuntimeException("Unable to read data file", e);
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void processQuestionnaire() {
        Questionnaire questionnairetest = injectorService.processQuestionnaire(this.questionnaire);
        System.out.println(questionnairetest.getId());
        assertNotNull(questionnairetest);
    }

    @Test
    public void inject() {
    }

    @Test
    public void inspect() {
    }
}