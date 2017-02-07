package de.scrum_master.app;

import de.scrum_master.thread_safe.ThreadSafeClassLoader;
import org.verapdf.core.EncryptedPdfException;
import org.verapdf.core.ModelParsingException;
import org.verapdf.core.ValidationException;
import org.verapdf.pdfa.PDFAParser;
import org.verapdf.pdfa.VeraGreenfieldFoundryProvider;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

import static de.scrum_master.thread_safe.ThreadSafeClassLoader.ObjectConstructionRules.forTargetType;

/**
 * Sample code from http://stackoverflow.com/questions/41936609, made thread-safe
 */
public class VeraPDFValidator implements Function<InputStream, byte[]> {
  public static boolean threadSafeMode = true;

  private static ThreadLocal<ThreadSafeClassLoader> threadSafeClassLoader =
    ThreadSafeClassLoader.create(           // Add one class per artifact for thread-safe classloader:
      VeraPDFValidatorHelper.class,         //   - our own helper class
      PDFAParser.class,                     //   - veraPDF core
      VeraGreenfieldFoundryProvider.class   //   - veraPDF validation-model
    );

  private String flavorId;
  private Boolean prettyXml;

  public VeraPDFValidator(String flavorId, Boolean prettyXml)
    throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    this.flavorId = flavorId;
    this.prettyXml = prettyXml;
  }

  @Override
  public byte[] apply(InputStream inputStream) {
    try {
      VeraPDFValidatorHelper validatorHelper = threadSafeMode
        ? threadSafeClassLoader.get().newObject(forTargetType(VeraPDFValidatorHelper.class))
        : new VeraPDFValidatorHelper();
      return validatorHelper.validatePDF(inputStream, flavorId, prettyXml);
    } catch (ModelParsingException | ValidationException | JAXBException | EncryptedPdfException e) {
      throw new RuntimeException("invoking veraPDF validation", e);
    }
  }
}
