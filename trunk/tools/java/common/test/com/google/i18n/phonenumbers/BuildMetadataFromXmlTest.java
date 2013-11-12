/*
 *  Copyright (C) 2011 The Libphonenumber Authors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.google.i18n.phonenumbers;

import com.google.i18n.phonenumbers.Phonemetadata.NumberFormat;
import com.google.i18n.phonenumbers.Phonemetadata.PhoneMetadata;
import com.google.i18n.phonenumbers.Phonemetadata.PhoneNumberDesc;

import junit.framework.TestCase;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.PatternSyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Unit tests for BuildMetadataFromXml.java
 *
 * @author Philippe Liard
 */
public class BuildMetadataFromXmlTest extends TestCase {

  // Helper method that outputs a DOM element from a XML string.
  private static Element parseXmlString(String xmlString)
      throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    InputSource inputSource = new InputSource();
    inputSource.setCharacterStream(new StringReader(xmlString));
    return documentBuilder.parse(inputSource).getDocumentElement();
  }

  // Tests validateRE().
  public void testValidateRERemovesWhiteSpaces() {
    String input = " hello world ";
    // Should remove all the white spaces contained in the provided string.
    assertEquals("helloworld", BuildMetadataFromXml.validateRE(input, true));
    // Make sure it only happens when the last parameter is set to true.
    assertEquals(" hello world ", BuildMetadataFromXml.validateRE(input, false));
  }

  public void testValidateREThrowsException() {
    String invalidPattern = "[";
    // Should throw an exception when an invalid pattern is provided independently of the last
    // parameter (remove white spaces).
    try {
      BuildMetadataFromXml.validateRE(invalidPattern, false);
      fail();
    } catch (PatternSyntaxException e) {
      // Test passed.
    }
    try {
      BuildMetadataFromXml.validateRE(invalidPattern, true);
      fail();
    } catch (PatternSyntaxException e) {
      // Test passed.
    }
    // We don't allow | to be followed by ) because it introduces bugs, since we typically use it at
    // the end of each line and when a line is deleted, if the pipe from the previous line is not
    // removed, we end up erroneously accepting an empty group as well.
    String patternWithPipeFollowedByClosingParentheses = "|)";
    try {
      BuildMetadataFromXml.validateRE(patternWithPipeFollowedByClosingParentheses, true);
      fail();
    } catch (PatternSyntaxException e) {
      // Test passed.
    }
    String patternWithPipeFollowedByNewLineAndClosingParentheses = "|\n)";
    try {
      BuildMetadataFromXml.validateRE(patternWithPipeFollowedByNewLineAndClosingParentheses, true);
      fail();
    } catch (PatternSyntaxException e) {
      // Test passed.
    }
  }

  public void testValidateRE() {
    String validPattern = "[a-zA-Z]d{1,9}";
    // The provided pattern should be left unchanged.
    assertEquals(validPattern, BuildMetadataFromXml.validateRE(validPattern, false));
  }

  // Tests getNationalPrefix().
  public void testGetNationalPrefix()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput = "<territory nationalPrefix='00'/>";
    Element territoryElement = parseXmlString(xmlInput);
    assertEquals("00", BuildMetadataFromXml.getNationalPrefix(territoryElement));
  }

  // Tests loadTerritoryTagMetadata().
  public void testLoadTerritoryTagMetadata()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput =
        "<territory countryCode='33' leadingDigits='2' internationalPrefix='00'" +
        "           preferredInternationalPrefix='0011' nationalPrefixForParsing='0'" +
        "           nationalPrefixTransformRule='9$1'" + // nationalPrefix manually injected.
        "           preferredExtnPrefix=' x' mainCountryForCode='true'" +
        "           leadingZeroPossible='true' mobileNumberPortableRegion='true'>" +
        "</territory>";
    Element territoryElement = parseXmlString(xmlInput);
    PhoneMetadata.Builder phoneMetadata =
        BuildMetadataFromXml.loadTerritoryTagMetadata("33", territoryElement, "0");
    assertEquals(33, phoneMetadata.getCountryCode());
    assertEquals("2", phoneMetadata.getLeadingDigits());
    assertEquals("00", phoneMetadata.getInternationalPrefix());
    assertEquals("0011", phoneMetadata.getPreferredInternationalPrefix());
    assertEquals("0", phoneMetadata.getNationalPrefixForParsing());
    assertEquals("9$1", phoneMetadata.getNationalPrefixTransformRule());
    assertEquals("0", phoneMetadata.getNationalPrefix());
    assertEquals(" x", phoneMetadata.getPreferredExtnPrefix());
    assertTrue(phoneMetadata.getMainCountryForCode());
    assertTrue(phoneMetadata.isLeadingZeroPossible());
    assertTrue(phoneMetadata.isMobileNumberPortableRegion());
  }

  public void testLoadTerritoryTagMetadataSetsBooleanFieldsToFalseByDefault()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput = "<territory countryCode='33'/>";
    Element territoryElement = parseXmlString(xmlInput);
    PhoneMetadata.Builder phoneMetadata =
        BuildMetadataFromXml.loadTerritoryTagMetadata("33", territoryElement, "");
    assertFalse(phoneMetadata.getMainCountryForCode());
    assertFalse(phoneMetadata.isLeadingZeroPossible());
    assertFalse(phoneMetadata.isMobileNumberPortableRegion());
  }

  public void testLoadTerritoryTagMetadataSetsNationalPrefixForParsingByDefault()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput = "<territory countryCode='33'/>";
    Element territoryElement = parseXmlString(xmlInput);
    PhoneMetadata.Builder phoneMetadata =
        BuildMetadataFromXml.loadTerritoryTagMetadata("33", territoryElement, "00");
    // When unspecified, nationalPrefixForParsing defaults to nationalPrefix.
    assertEquals("00", phoneMetadata.getNationalPrefix());
    assertEquals(phoneMetadata.getNationalPrefix(), phoneMetadata.getNationalPrefixForParsing());
  }

  public void testLoadTerritoryTagMetadataWithRequiredAttributesOnly()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput = "<territory countryCode='33' internationalPrefix='00'/>";
    Element territoryElement = parseXmlString(xmlInput);
    // Should not throw any exception.
    BuildMetadataFromXml.loadTerritoryTagMetadata("33", territoryElement, "");
  }

  // Tests loadInternationalFormat().
  public void testLoadInternationalFormat()
      throws ParserConfigurationException, SAXException, IOException {
    String intlFormat = "$1 $2";
    String xmlInput = "<numberFormat><intlFormat>" + intlFormat + "</intlFormat></numberFormat>";
    Element numberFormatElement = parseXmlString(xmlInput);
    PhoneMetadata.Builder metadata = PhoneMetadata.newBuilder();
    NumberFormat nationalFormat = NumberFormat.newBuilder().build();

    assertTrue(BuildMetadataFromXml.loadInternationalFormat(metadata, numberFormatElement,
                                                            nationalFormat));
    assertEquals(intlFormat, metadata.getIntlNumberFormat(0).getFormat());
  }

  public void testLoadInternationalFormatWithBothNationalAndIntlFormatsDefined()
      throws ParserConfigurationException, SAXException, IOException {
    String intlFormat = "$1 $2";
    String xmlInput = "<numberFormat><intlFormat>" + intlFormat + "</intlFormat></numberFormat>";
    Element numberFormatElement = parseXmlString(xmlInput);
    PhoneMetadata.Builder metadata = PhoneMetadata.newBuilder();
    NumberFormat.Builder nationalFormat = NumberFormat.newBuilder();
    nationalFormat.setFormat("$1");

    assertTrue(BuildMetadataFromXml.loadInternationalFormat(metadata, numberFormatElement,
                                                            nationalFormat.build()));
    assertEquals(intlFormat, metadata.getIntlNumberFormat(0).getFormat());
  }

  public void testLoadInternationalFormatExpectsOnlyOnePattern()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput = "<numberFormat><intlFormat/><intlFormat/></numberFormat>";
    Element numberFormatElement = parseXmlString(xmlInput);
    PhoneMetadata.Builder metadata = PhoneMetadata.newBuilder();

    // Should throw an exception as multiple intlFormats are provided.
    try {
      BuildMetadataFromXml.loadInternationalFormat(metadata, numberFormatElement,
                                                   NumberFormat.newBuilder().build());
      fail();
    } catch (RuntimeException e) {
      // Test passed.
    }
  }

  public void testLoadInternationalFormatUsesNationalFormatByDefault()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput = "<numberFormat></numberFormat>";
    Element numberFormatElement = parseXmlString(xmlInput);
    PhoneMetadata.Builder metadata = PhoneMetadata.newBuilder();
    NumberFormat.Builder nationalFormat = NumberFormat.newBuilder();
    String nationalPattern = "$1 $2 $3";
    nationalFormat.setFormat(nationalPattern);

    assertFalse(BuildMetadataFromXml.loadInternationalFormat(metadata, numberFormatElement,
                                                             nationalFormat.build()));
    assertEquals(nationalPattern, metadata.getIntlNumberFormat(0).getFormat());
  }

  public void testLoadInternationalFormatCopiesNationalFormatData()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput = "<numberFormat></numberFormat>";
    Element numberFormatElement = parseXmlString(xmlInput);
    PhoneMetadata.Builder metadata = PhoneMetadata.newBuilder();
    NumberFormat.Builder nationalFormat = NumberFormat.newBuilder();
    nationalFormat.setFormat("$1-$2");
    nationalFormat.setNationalPrefixOptionalWhenFormatting(true);

    assertFalse(BuildMetadataFromXml.loadInternationalFormat(metadata, numberFormatElement,
                                                             nationalFormat.build()));
    assertTrue(metadata.getIntlNumberFormat(0).isNationalPrefixOptionalWhenFormatting());
  }

  public void testLoadNationalFormat()
      throws ParserConfigurationException, SAXException, IOException {
    String nationalFormat = "$1 $2";
    String xmlInput = String.format("<numberFormat><format>%s</format></numberFormat>",
                                    nationalFormat);
    Element numberFormatElement = parseXmlString(xmlInput);
    PhoneMetadata.Builder metadata = PhoneMetadata.newBuilder();
    NumberFormat.Builder numberFormat = NumberFormat.newBuilder();
    BuildMetadataFromXml.loadNationalFormat(metadata, numberFormatElement, numberFormat);
    assertEquals(nationalFormat, numberFormat.getFormat());
  }

  public void testLoadNationalFormatRequiresFormat()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput = "<numberFormat></numberFormat>";
    Element numberFormatElement = parseXmlString(xmlInput);
    PhoneMetadata.Builder metadata = PhoneMetadata.newBuilder();
    NumberFormat.Builder numberFormat = NumberFormat.newBuilder();

    try {
      BuildMetadataFromXml.loadNationalFormat(metadata, numberFormatElement, numberFormat);
      fail();
    } catch (RuntimeException e) {
      // Test passed.
    }
  }

  public void testLoadNationalFormatExpectsExactlyOneFormat()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput = "<numberFormat><format/><format/></numberFormat>";
    Element numberFormatElement = parseXmlString(xmlInput);
    PhoneMetadata.Builder metadata = PhoneMetadata.newBuilder();
    NumberFormat.Builder numberFormat = NumberFormat.newBuilder();

    try {
      BuildMetadataFromXml.loadNationalFormat(metadata, numberFormatElement, numberFormat);
      fail();
    } catch (RuntimeException e) {
      // Test passed.
    }
  }

  // Tests loadAvailableFormats().
  public void testLoadAvailableFormats()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput =
        "<territory >" +
        "  <availableFormats>" +
        "    <numberFormat nationalPrefixFormattingRule='($FG)'" +
        "                  carrierCodeFormattingRule='$NP $CC ($FG)'>" +
        "      <format>$1 $2 $3</format>" +
        "    </numberFormat>" +
        "  </availableFormats>" +
        "</territory>";
    Element element = parseXmlString(xmlInput);
    PhoneMetadata.Builder metadata = PhoneMetadata.newBuilder();
    BuildMetadataFromXml.loadAvailableFormats(
        metadata, element, "0", "", false /* NP not optional */);
    assertEquals("($1)", metadata.getNumberFormat(0).getNationalPrefixFormattingRule());
    assertEquals("0 $CC ($1)", metadata.getNumberFormat(0).getDomesticCarrierCodeFormattingRule());
    assertEquals("$1 $2 $3", metadata.getNumberFormat(0).getFormat());
  }

  public void testLoadAvailableFormatsPropagatesCarrierCodeFormattingRule()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput =
        "<territory carrierCodeFormattingRule='$NP $CC ($FG)'>" +
        "  <availableFormats>" +
        "    <numberFormat nationalPrefixFormattingRule='($FG)'>" +
        "      <format>$1 $2 $3</format>" +
        "    </numberFormat>" +
        "  </availableFormats>" +
        "</territory>";
    Element element = parseXmlString(xmlInput);
    PhoneMetadata.Builder metadata = PhoneMetadata.newBuilder();
    BuildMetadataFromXml.loadAvailableFormats(
        metadata, element, "0", "", false /* NP not optional */);
    assertEquals("($1)", metadata.getNumberFormat(0).getNationalPrefixFormattingRule());
    assertEquals("0 $CC ($1)", metadata.getNumberFormat(0).getDomesticCarrierCodeFormattingRule());
    assertEquals("$1 $2 $3", metadata.getNumberFormat(0).getFormat());
  }

  public void testLoadAvailableFormatsSetsProvidedNationalPrefixFormattingRule()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput =
        "<territory>" +
        "  <availableFormats>" +
        "    <numberFormat><format>$1 $2 $3</format></numberFormat>" +
        "  </availableFormats>" +
        "</territory>";
    Element element = parseXmlString(xmlInput);
    PhoneMetadata.Builder metadata = PhoneMetadata.newBuilder();
    BuildMetadataFromXml.loadAvailableFormats(
        metadata, element, "", "($1)", false /* NP not optional */);
    assertEquals("($1)", metadata.getNumberFormat(0).getNationalPrefixFormattingRule());
  }

  public void testLoadAvailableFormatsClearsIntlFormat()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput =
        "<territory>" +
        "  <availableFormats>" +
        "    <numberFormat><format>$1 $2 $3</format></numberFormat>" +
        "  </availableFormats>" +
        "</territory>";
    Element element = parseXmlString(xmlInput);
    PhoneMetadata.Builder metadata = PhoneMetadata.newBuilder();
    BuildMetadataFromXml.loadAvailableFormats(
        metadata, element, "0", "($1)", false /* NP not optional */);
    assertEquals(0, metadata.intlNumberFormatSize());
  }

  public void testLoadAvailableFormatsHandlesMultipleNumberFormats()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput =
        "<territory>" +
        "  <availableFormats>" +
        "    <numberFormat><format>$1 $2 $3</format></numberFormat>" +
        "    <numberFormat><format>$1-$2</format></numberFormat>" +
        "  </availableFormats>" +
        "</territory>";
    Element element = parseXmlString(xmlInput);
    PhoneMetadata.Builder metadata = PhoneMetadata.newBuilder();
    BuildMetadataFromXml.loadAvailableFormats(
        metadata, element, "0", "($1)", false /* NP not optional */);
    assertEquals("$1 $2 $3", metadata.getNumberFormat(0).getFormat());
    assertEquals("$1-$2", metadata.getNumberFormat(1).getFormat());
  }

  public void testLoadInternationalFormatDoesNotSetIntlFormatWhenNA()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput = "<numberFormat><intlFormat>NA</intlFormat></numberFormat>";
    Element numberFormatElement = parseXmlString(xmlInput);
    PhoneMetadata.Builder metadata = PhoneMetadata.newBuilder();
    NumberFormat.Builder nationalFormat = NumberFormat.newBuilder();
    nationalFormat.setFormat("$1 $2");

    BuildMetadataFromXml.loadInternationalFormat(metadata, numberFormatElement,
                                                 nationalFormat.build());
    assertEquals(0, metadata.intlNumberFormatSize());
  }

  // Tests setLeadingDigitsPatterns().
  public void testSetLeadingDigitsPatterns()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput =
        "<numberFormat>" +
        "<leadingDigits>1</leadingDigits><leadingDigits>2</leadingDigits>" +
        "</numberFormat>";
    Element numberFormatElement = parseXmlString(xmlInput);
    NumberFormat.Builder numberFormat = NumberFormat.newBuilder();
    BuildMetadataFromXml.setLeadingDigitsPatterns(numberFormatElement, numberFormat);

    assertEquals("1", numberFormat.getLeadingDigitsPattern(0));
    assertEquals("2", numberFormat.getLeadingDigitsPattern(1));
  }

  // Tests getNationalPrefixFormattingRuleFromElement().
  public void testGetNationalPrefixFormattingRuleFromElement()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput = "<territory nationalPrefixFormattingRule='$NP$FG'/>";
    Element element = parseXmlString(xmlInput);
    assertEquals("0$1",
                 BuildMetadataFromXml.getNationalPrefixFormattingRuleFromElement(element, "0"));
  }

  // Tests getDomesticCarrierCodeFormattingRuleFromElement().
  public void testGetDomesticCarrierCodeFormattingRuleFromElement()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput = "<territory carrierCodeFormattingRule='$NP$CC $FG'/>";
    Element element = parseXmlString(xmlInput);
    assertEquals("0$CC $1",
                 BuildMetadataFromXml.getDomesticCarrierCodeFormattingRuleFromElement(element,
                                                                                      "0"));
  }

  // Tests isValidNumberType().
  public void testIsValidNumberTypeWithInvalidInput() {
    assertFalse(BuildMetadataFromXml.isValidNumberType("invalidType"));
  }

  // Tests processPhoneNumberDescElement().
  public void testProcessPhoneNumberDescElementWithInvalidInput()
      throws ParserConfigurationException, SAXException, IOException {
    PhoneNumberDesc.Builder generalDesc = PhoneNumberDesc.newBuilder();
    Element territoryElement = parseXmlString("<territory/>");
    PhoneNumberDesc.Builder phoneNumberDesc;

    phoneNumberDesc = BuildMetadataFromXml.processPhoneNumberDescElement(
        generalDesc, territoryElement, "invalidType", false);
    assertEquals("NA", phoneNumberDesc.getPossibleNumberPattern());
    assertEquals("NA", phoneNumberDesc.getNationalNumberPattern());
  }

  public void testProcessPhoneNumberDescElementMergesWithGeneralDesc()
      throws ParserConfigurationException, SAXException, IOException {
    PhoneNumberDesc.Builder generalDesc = PhoneNumberDesc.newBuilder();
    generalDesc.setPossibleNumberPattern("\\d{6}");
    Element territoryElement = parseXmlString("<territory><fixedLine/></territory>");
    PhoneNumberDesc.Builder phoneNumberDesc;

    phoneNumberDesc = BuildMetadataFromXml.processPhoneNumberDescElement(
        generalDesc, territoryElement, "fixedLine", false);
    assertEquals("\\d{6}", phoneNumberDesc.getPossibleNumberPattern());
  }

  public void testProcessPhoneNumberDescElementOverridesGeneralDesc()
      throws ParserConfigurationException, SAXException, IOException {
    PhoneNumberDesc.Builder generalDesc = PhoneNumberDesc.newBuilder();
    generalDesc.setPossibleNumberPattern("\\d{8}");
    String xmlInput =
        "<territory><fixedLine>" +
        "  <possibleNumberPattern>\\d{6}</possibleNumberPattern>" +
        "</fixedLine></territory>";
    Element territoryElement = parseXmlString(xmlInput);
    PhoneNumberDesc.Builder phoneNumberDesc;

    phoneNumberDesc = BuildMetadataFromXml.processPhoneNumberDescElement(
        generalDesc, territoryElement, "fixedLine", false);
    assertEquals("\\d{6}", phoneNumberDesc.getPossibleNumberPattern());
  }

  public void testProcessPhoneNumberDescElementHandlesLiteBuild()
      throws ParserConfigurationException, SAXException, IOException {
    PhoneNumberDesc.Builder generalDesc = PhoneNumberDesc.newBuilder();
    String xmlInput =
        "<territory><fixedLine>" +
        "  <exampleNumber>01 01 01 01</exampleNumber>" +
        "</fixedLine></territory>";
    Element territoryElement = parseXmlString(xmlInput);
    PhoneNumberDesc.Builder phoneNumberDesc;

    phoneNumberDesc = BuildMetadataFromXml.processPhoneNumberDescElement(
        generalDesc, territoryElement, "fixedLine", true);
    assertEquals("", phoneNumberDesc.getExampleNumber());
  }

  public void testProcessPhoneNumberDescOutputsExampleNumberByDefault()
      throws ParserConfigurationException, SAXException, IOException {
    PhoneNumberDesc.Builder generalDesc = PhoneNumberDesc.newBuilder();
    String xmlInput =
        "<territory><fixedLine>" +
        "  <exampleNumber>01 01 01 01</exampleNumber>" +
        "</fixedLine></territory>";
    Element territoryElement = parseXmlString(xmlInput);
    PhoneNumberDesc.Builder phoneNumberDesc;

    phoneNumberDesc = BuildMetadataFromXml.processPhoneNumberDescElement(
        generalDesc, territoryElement, "fixedLine", false);
    assertEquals("01 01 01 01", phoneNumberDesc.getExampleNumber());
  }

  public void testProcessPhoneNumberDescRemovesWhiteSpacesInPatterns()
      throws ParserConfigurationException, SAXException, IOException {
    PhoneNumberDesc.Builder generalDesc = PhoneNumberDesc.newBuilder();
    String xmlInput =
        "<territory><fixedLine>" +
        "  <possibleNumberPattern>\t \\d { 6 } </possibleNumberPattern>" +
        "</fixedLine></territory>";
    Element countryElement = parseXmlString(xmlInput);
    PhoneNumberDesc.Builder phoneNumberDesc;

    phoneNumberDesc = BuildMetadataFromXml.processPhoneNumberDescElement(
        generalDesc, countryElement, "fixedLine", false);
    assertEquals("\\d{6}", phoneNumberDesc.getPossibleNumberPattern());
  }

  // Tests setRelevantDescPatterns().
  public void testSetRelevantDescPatternsSetsSameMobileAndFixedLinePattern()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput =
        "<territory countryCode=\"33\">" +
        "  <fixedLine><nationalNumberPattern>\\d{6}</nationalNumberPattern></fixedLine>" +
        "  <mobile><nationalNumberPattern>\\d{6}</nationalNumberPattern></mobile>" +
        "</territory>";
    Element territoryElement = parseXmlString(xmlInput);
    PhoneMetadata.Builder metadata = PhoneMetadata.newBuilder();
    // Should set sameMobileAndFixedPattern to true.
    BuildMetadataFromXml.setRelevantDescPatterns(metadata, territoryElement, false /* liteBuild */,
        false /* isShortNumberMetadata */);
    assertTrue(metadata.isSameMobileAndFixedLinePattern());
  }

  public void testSetRelevantDescPatternsSetsAllDescriptionsForRegularLengthNumbers()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput =
        "<territory countryCode=\"33\">" +
        "  <fixedLine><nationalNumberPattern>\\d{1}</nationalNumberPattern></fixedLine>" +
        "  <mobile><nationalNumberPattern>\\d{2}</nationalNumberPattern></mobile>" +
        "  <pager><nationalNumberPattern>\\d{3}</nationalNumberPattern></pager>" +
        "  <tollFree><nationalNumberPattern>\\d{4}</nationalNumberPattern></tollFree>" +
        "  <premiumRate><nationalNumberPattern>\\d{5}</nationalNumberPattern></premiumRate>" +
        "  <sharedCost><nationalNumberPattern>\\d{6}</nationalNumberPattern></sharedCost>" +
        "  <personalNumber><nationalNumberPattern>\\d{7}</nationalNumberPattern></personalNumber>" +
        "  <voip><nationalNumberPattern>\\d{8}</nationalNumberPattern></voip>" +
        "  <uan><nationalNumberPattern>\\d{9}</nationalNumberPattern></uan>" +
        "</territory>";
    Element territoryElement = parseXmlString(xmlInput);
    PhoneMetadata.Builder metadata = PhoneMetadata.newBuilder();
    BuildMetadataFromXml.setRelevantDescPatterns(metadata, territoryElement, false /* liteBuild */,
        false /* isShortNumberMetadata */);
    assertEquals("\\d{1}", metadata.getFixedLine().getNationalNumberPattern());
    assertEquals("\\d{2}", metadata.getMobile().getNationalNumberPattern());
    assertEquals("\\d{3}", metadata.getPager().getNationalNumberPattern());
    assertEquals("\\d{4}", metadata.getTollFree().getNationalNumberPattern());
    assertEquals("\\d{5}", metadata.getPremiumRate().getNationalNumberPattern());
    assertEquals("\\d{6}", metadata.getSharedCost().getNationalNumberPattern());
    assertEquals("\\d{7}", metadata.getPersonalNumber().getNationalNumberPattern());
    assertEquals("\\d{8}", metadata.getVoip().getNationalNumberPattern());
    assertEquals("\\d{9}", metadata.getUan().getNationalNumberPattern());
  }

  public void testSetRelevantDescPatternsSetsAllDescriptionsForShortNumbers()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput =
        "<territory ID=\"FR\">" +
        "  <tollFree><nationalNumberPattern>\\d{1}</nationalNumberPattern></tollFree>" +
        "  <standardRate><nationalNumberPattern>\\d{2}</nationalNumberPattern></standardRate>" +
        "  <premiumRate><nationalNumberPattern>\\d{3}</nationalNumberPattern></premiumRate>" +
        "  <shortCode><nationalNumberPattern>\\d{4}</nationalNumberPattern></shortCode>" +
        "  <carrierSpecific>" +
        "    <nationalNumberPattern>\\d{5}</nationalNumberPattern>" +
        "  </carrierSpecific>" +
        "</territory>";
    Element territoryElement = parseXmlString(xmlInput);
    PhoneMetadata.Builder metadata = PhoneMetadata.newBuilder();
    BuildMetadataFromXml.setRelevantDescPatterns(metadata, territoryElement, false /* liteBuild */,
        true /* isShortNumberMetadata */);
    assertEquals("\\d{1}", metadata.getTollFree().getNationalNumberPattern());
    assertEquals("\\d{2}", metadata.getStandardRate().getNationalNumberPattern());
    assertEquals("\\d{3}", metadata.getPremiumRate().getNationalNumberPattern());
    assertEquals("\\d{4}", metadata.getShortCode().getNationalNumberPattern());
    assertEquals("\\d{5}", metadata.getCarrierSpecific().getNationalNumberPattern());
  }

  public void testAlternateFormatsOmitsDescPatterns()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput =
        "<territory countryCode=\"33\">" +
        "  <availableFormats>" +
        "    <numberFormat pattern=\"(1)(\\d{3})\">" +
        "      <leadingDigits>1</leadingDigits>" +
        "      <format>$1</format>" +
        "    </numberFormat>" +
        "  </availableFormats>" +
        "  <fixedLine><nationalNumberPattern>\\d{1}</nationalNumberPattern></fixedLine>" +
        "  <shortCode><nationalNumberPattern>\\d{2}</nationalNumberPattern></shortCode>" +
        "</territory>";
    Element territoryElement = parseXmlString(xmlInput);
    PhoneMetadata metadata = BuildMetadataFromXml.loadCountryMetadata("FR", territoryElement,
        false /* liteBuild */, false /* isShortNumberMetadata */,
        true /* isAlternateFormatsMetadata */);
    assertEquals("(1)(\\d{3})", metadata.getNumberFormat(0).getPattern());
    assertEquals("1", metadata.getNumberFormat(0).getLeadingDigitsPattern(0));
    assertEquals("$1", metadata.getNumberFormat(0).getFormat());
    assertFalse(metadata.hasFixedLine());
    assertNull(metadata.getFixedLine());
    assertFalse(metadata.hasShortCode());
    assertNull(metadata.getShortCode());
  }

  public void testNationalPrefixRulesSetCorrectly()
      throws ParserConfigurationException, SAXException, IOException {
    String xmlInput =
        "<territory countryCode=\"33\" nationalPrefix=\"0\"" +
        " nationalPrefixFormattingRule=\"$NP$FG\">" +
        "  <availableFormats>" +
        "    <numberFormat pattern=\"(1)(\\d{3})\" nationalPrefixOptionalWhenFormatting=\"true\">" +
        "      <leadingDigits>1</leadingDigits>" +
        "      <format>$1</format>" +
        "    </numberFormat>" +
        "    <numberFormat pattern=\"(\\d{3})\" nationalPrefixOptionalWhenFormatting=\"false\">" +
        "      <leadingDigits>2</leadingDigits>" +
        "      <format>$1</format>" +
        "    </numberFormat>" +
        "  </availableFormats>" +
        "  <fixedLine><nationalNumberPattern>\\d{1}</nationalNumberPattern></fixedLine>" +
        "</territory>";
    Element territoryElement = parseXmlString(xmlInput);
    PhoneMetadata metadata = BuildMetadataFromXml.loadCountryMetadata("FR", territoryElement,
        false /* liteBuild */, false /* isShortNumberMetadata */,
        true /* isAlternateFormatsMetadata */);
    assertTrue(metadata.getNumberFormat(0).isNationalPrefixOptionalWhenFormatting());
    // This is inherited from the territory, with $NP replaced by the actual national prefix, and
    // $FG replaced with $1.
    assertEquals("0$1", metadata.getNumberFormat(0).getNationalPrefixFormattingRule());
    // Here it is explicitly set to false.
    assertFalse(metadata.getNumberFormat(1).isNationalPrefixOptionalWhenFormatting());
  }
}
