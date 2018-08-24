# Code

## InjectService

```
    private Questionnaire processExtensions(Questionnaire questionnaire, List<Extension> extensions) {
        for (Extension extension : extensions) {
            IBaseDatatype iBaseDatatype = extension.getValue();
            Reference reference = (Reference) iBaseDatatype;
            String extension_value = reference.getReference();
            log.info("Reading Extension: {}", extension_value);
            if (extension_value.contains(uri)) {
                return injectFile(questionnaire, extension_value, uri);
            } else {
                return injectLocalDataElement(questionnaire, extension_value, uri);
            }
        }
        return questionnaire;
    }

    private List<Extension> getDemaps(Questionnaire.QuestionnaireItemComponent item) {
        List<Extension> extensions = new ArrayList();
        if (item.getExtensionsByUrl(demap) != null) {
            extensions = item.getExtensionsByUrl(demap);
        }
        return extensions;
    }

        public boolean inspect(Questionnaire.QuestionnaireItemComponent item) {
            return item.getExtensionsByUrl(demap) != null;
        }

        private Questionnaire injectFile(Questionnaire questionnaire, String extension_value, String uri) {
            String sourceFile = extension_value.replace(uri, "FHIRForms/").concat(".json");
            // The segment below is from ResourceInjector.java
            log.info("About to inject: {}", sourceFile);
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
                // In this case the resource is a DataElement
                DataElement dataElement = (DataElement) resource;
                questionnaire = inject(questionnaire, dataElement);

            } catch (final IOException e) {
                throw new RuntimeException("Unable to read data file", e);
            }
            return questionnaire;
        }

        private Questionnaire injectLocalDataElement(Questionnaire questionnaire, String extension_value, String uri) {
            log.info("Reading Extension: {}", extension_value);
            DataElement dataElement;
            if (urlValidator(uri)) {
                dataElement = fhirClient.read().resource(DataElement.class).withUrl(uri).execute();
            } else {
                dataElement = fhirClient.read().resource(DataElement.class).withId(uri).execute();
            }
            log.info("About to inject: {}", uri);
            final FhirContext ctx = FhirContext.forDstu3();
            ctx.setParserErrorHandler(new StrictErrorHandler());
            final IParser parser = ctx.newJsonParser();
            parser.setPrettyPrint(true);
            questionnaire = inject(questionnaire, dataElement);
            return questionnaire;
        }



                    final FhirContext ctx = FhirContext.forDstu3();
                            ctx.setParserErrorHandler(new StrictErrorHandler());
                            final IParser parser = ctx.newJsonParser();
                            parser.setPrettyPrint(true);
```