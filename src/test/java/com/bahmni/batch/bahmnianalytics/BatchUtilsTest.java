package com.bahmni.batch.bahmnianalytics;

import com.bahmni.batch.bahmnianalytics.exception.BatchResourceException;
import com.bahmni.batch.bahmnianalytics.util.BatchUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.*;

@PrepareForTest(IOUtils.class)
@RunWith(PowerMockRunner.class)
public class BatchUtilsTest {

    @Rule
    ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(IOUtils.class);
    }

    @Test
    public void ensureThatTheCommaSeparatedConceptNamesAreConvertedToSet() {
        List<String> conceptNames = BatchUtils.convertConceptNamesToSet("\"a,b\",\"c\",\"d\"");

        assertEquals(3, conceptNames.size());
        assertTrue(conceptNames.contains("c"));
        assertTrue(conceptNames.contains("d"));
        assertTrue(conceptNames.contains("a,b"));
    }


    @Test
    public void ensureThatSetIsNotNullWhenConceptNamesIsEmpty() {
        List<String> conceptNames = BatchUtils.convertConceptNamesToSet("");
        assertNotNull(conceptNames);
        assertEquals(0, conceptNames.size());
    }

    @Test
    public void ensureThatSetIsNotNullWhenConceptNamesIsNull() {
        List<String> conceptNames = BatchUtils.convertConceptNamesToSet(null);
        assertNotNull(conceptNames);
        assertEquals(0, conceptNames.size());
    }

    @Test
    public void shouldConvertResourceOutputToString() throws Exception {
        ClassPathResource classPathResource = Mockito.mock(ClassPathResource.class);
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(classPathResource.getInputStream()).thenReturn(inputStream);
        String expectedString = "stringEquivalentOfClassPathResource";
        Mockito.when(IOUtils.toString(inputStream)).thenReturn(expectedString);

        String actualString = BatchUtils.convertResourceOutputToString(classPathResource);

        assertEquals(expectedString, actualString);
        Mockito.verify(classPathResource, Mockito.times(1)).getInputStream();
    }

    @Test
    public void shouldThrowBatchResourceException() throws Exception {
        expectedException.expect(BatchResourceException.class);
        expectedException.expectMessage("Cannot load the provided resource. Unable to continue");

        ClassPathResource classPathResource = Mockito.mock(ClassPathResource.class);
        Mockito.when(classPathResource.getInputStream()).thenThrow(new IOException());

        BatchUtils.convertResourceOutputToString(classPathResource);
    }

    @Test
    public void shouldReturnPostgresCompatibleValueForDataTypeText() throws Exception {
        String value = "some text";
        String typeText = "text";

        String postgresCompatibleValue = BatchUtils.getPostgresCompatibleValue(value, typeText);

        assertEquals("'some text'", postgresCompatibleValue);
    }

    @Test
    public void shouldReturnPostgresCompatibleValueForDataTypeDate() throws Exception {
        String value = "12/12/2017";
        String typeDate = "date";

        String postgresCompatibleValue = BatchUtils.getPostgresCompatibleValue(value, typeDate);

        assertEquals("'12/12/2017'", postgresCompatibleValue);
    }

    @Test
    public void shouldReturnPostgresCompatibleValueForDataTypeTimestamp() throws Exception {
        String value = "2015-01-22 00:00:00.0";
        String typeTimestamp = "timestamp";

        String postgresCompatibleValue = BatchUtils.getPostgresCompatibleValue(value, typeTimestamp);

        assertEquals("'2015-01-22 00:00:00.0'", postgresCompatibleValue);
    }

    @Test
    public void shouldReturnSameValueForAnyOtherDataTypes() throws Exception {
        String value = "some value";
        String otherDatatype = "otherDataType";

        String postgresCompatibleValue = BatchUtils.getPostgresCompatibleValue(value, otherDatatype);

        assertEquals(value, postgresCompatibleValue);
    }

}
