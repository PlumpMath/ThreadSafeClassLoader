package de.scrum_master.app;

import org.verapdf.core.EncryptedPdfException;
import org.verapdf.core.ModelParsingException;
import org.verapdf.core.ValidationException;
import org.verapdf.core.XmlSerialiser;
import org.verapdf.pdfa.Foundries;
import org.verapdf.pdfa.PDFAParser;
import org.verapdf.pdfa.PDFAValidator;
import org.verapdf.pdfa.VeraGreenfieldFoundryProvider;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.results.ValidationResult;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Sample code from http://stackoverflow.com/questions/41936609
 */
public class VeraPDFValidatorHelper {
  public byte[] validatePDF(InputStream inputStream, String flavorId, Boolean prettyXml)
    throws ModelParsingException, ValidationException, JAXBException, EncryptedPdfException
  {
    VeraGreenfieldFoundryProvider.initialise();
    PDFAFlavour flavour = PDFAFlavour.byFlavourId(flavorId);
    PDFAValidator validator = Foundries.defaultInstance().createValidator(flavour, false);
    PDFAParser loader = Foundries.defaultInstance().createParser(inputStream, flavour);
    ValidationResult result = validator.validate(loader);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    XmlSerialiser.toXml(result, baos, prettyXml, false);
    return baos.toByteArray();
  }
}
