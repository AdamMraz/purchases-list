package ru.adamdev.purchases.list.helper.impl;

import lombok.Getter;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.adamdev.purchases.list.exception.PurchasesListException;
import ru.adamdev.purchases.list.function.ThrowableSupplier;
import ru.adamdev.purchases.list.helper.PurchasesListExceptionWrapper;
import ru.adamdev.purchases.list.model.ExceptionModel;
import ru.adamdev.purchases.list.util.FileUtil;
import ru.adamdev.purchases.list.util.JsonUtil;

import java.io.File;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class PurchasesListExceptionWrapperImplTest {

    private static final TestObject TEST_OBJECT = new TestObject();
    private static final File FILE = new File("");
    private static final String ERROR_TYPE = UUID.randomUUID().toString();
    private static final String ERROR_MESSAGE = UUID.randomUUID().toString();

    @Mock
    private Appender mockAppender;
    @Spy
    private PurchasesListExceptionWrapper wrapper = new PurchasesListExceptionWrapperImpl();

    @Getter
    static class TestObject {
        private final String testString = "TEST_STRING";
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void wrapWithIsLogIfSuccessOk(boolean isLogIfSuccess) throws PurchasesListException {
        ThrowableSupplier<TestObject, PurchasesListException> throwableSupplier = Mockito.mock(ThrowableSupplier.class);
        when(throwableSupplier.get()).thenReturn(TEST_OBJECT);
        try (MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(FileUtil.class)) {
            TestObject result = wrapper.wrap(throwableSupplier, FILE, isLogIfSuccess);
            assertEquals(TEST_OBJECT, result);
            int fileUtilWriteToFileCount = isLogIfSuccess ? 1 : 0;
            verify(throwableSupplier).get();
            fileUtilMockedStatic.verify(() -> FileUtil.writeToFile(TEST_OBJECT, FILE), times(fileUtilWriteToFileCount));
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void wrapWithIsLogIfSuccessException(boolean isLogIfSuccess) throws Exception {
        ThrowableSupplier<TestObject, Exception> throwableSupplier = Mockito.mock(ThrowableSupplier.class);
        when(throwableSupplier.get()).thenThrow(new PurchasesListException(ERROR_TYPE, ERROR_MESSAGE));
        ExceptionModel exceptionModel = new ExceptionModel(ERROR_TYPE, ERROR_MESSAGE);
        try (MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(FileUtil.class)) {
            TestObject result = wrapper.wrap(throwableSupplier, FILE, isLogIfSuccess);
            assertNull(result);
            verify(throwableSupplier).get();
            fileUtilMockedStatic.verify(() -> FileUtil.writeToFile(exceptionModel, FILE));
        }
    }

    @Test
    void wrapWithOutputFileOk() throws PurchasesListException {
        ThrowableSupplier<TestObject, PurchasesListException> throwableSupplier = Mockito.mock(ThrowableSupplier.class);
        when(throwableSupplier.get()).thenReturn(TEST_OBJECT);
        try (MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(FileUtil.class)) {
            TestObject result = wrapper.wrap(throwableSupplier, FILE);
            assertEquals(TEST_OBJECT, result);
            verify(throwableSupplier).get();
            fileUtilMockedStatic.verify(() -> FileUtil.writeToFile(TEST_OBJECT, FILE));
        }
    }

    @Test
    void wrapWithOutputFileException() throws Exception {
        ThrowableSupplier<TestObject, Exception> throwableSupplier = Mockito.mock(ThrowableSupplier.class);
        when(throwableSupplier.get()).thenThrow(new PurchasesListException(ERROR_TYPE, ERROR_MESSAGE));
        ExceptionModel exceptionModel = new ExceptionModel(ERROR_TYPE, ERROR_MESSAGE);
        try (MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(FileUtil.class)) {
            TestObject result = wrapper.wrap(throwableSupplier, FILE);
            assertNull(result);
            verify(throwableSupplier).get();
            fileUtilMockedStatic.verify(() -> FileUtil.writeToFile(exceptionModel, FILE));
        }
    }

    @Test
    void wrapWithLogOk() throws PurchasesListException {
        ThrowableSupplier<TestObject, PurchasesListException> throwableSupplier = Mockito.mock(ThrowableSupplier.class);
        when(throwableSupplier.get()).thenReturn(TEST_OBJECT);
        Logger logger = Logger.getLogger(PurchasesListExceptionWrapper.class);
        logger.addAppender(mockAppender);
        TestObject result = wrapper.wrap(throwableSupplier);
        String jsonResult = JsonUtil.serialize(result);
        assertEquals(TEST_OBJECT, result);
        verify(throwableSupplier).get();
        ArgumentCaptor<LoggingEvent> eventArgumentCaptor = ArgumentCaptor.forClass(LoggingEvent.class);
        verify(mockAppender).doAppend(eventArgumentCaptor.capture());
        String logMessage = String.valueOf(eventArgumentCaptor.getAllValues().get(0).getMessage());
        assertNotNull(logMessage);
        assertEquals(jsonResult, logMessage);
    }

    @Test
    void wrapWithLogException() throws Exception {
        ThrowableSupplier<TestObject, PurchasesListException> throwableSupplier = Mockito.mock(ThrowableSupplier.class);
        ExceptionModel exceptionModel = new ExceptionModel(ERROR_TYPE, ERROR_MESSAGE);
        String jsonResult = JsonUtil.serialize(exceptionModel);
        when(throwableSupplier.get()).thenThrow(new PurchasesListException(ERROR_TYPE, ERROR_MESSAGE));
        Logger logger = Logger.getLogger(PurchasesListExceptionWrapper.class);
        logger.addAppender(mockAppender);
        TestObject result = wrapper.wrap(throwableSupplier);
        assertNull(result);
        verify(throwableSupplier).get();
        ArgumentCaptor<LoggingEvent> eventArgumentCaptor = ArgumentCaptor.forClass(LoggingEvent.class);
        verify(mockAppender, times(2)).doAppend(eventArgumentCaptor.capture());
        String logMessage = String.valueOf(eventArgumentCaptor.getAllValues().get(0).getMessage());
        assertNotNull(logMessage);
        assertEquals(ERROR_MESSAGE, logMessage);
        logMessage = String.valueOf(eventArgumentCaptor.getAllValues().get(1).getMessage());
        assertNotNull(logMessage);
        assertEquals(jsonResult, logMessage);
    }
}