/*
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.i18n.phonenumbers;

import junit.framework.TestCase;

import java.io.InputStream;

/**
 * Unit tests for PhoneNumberUtil.java
 *
 * Note that these tests use the metadata contained in the file specified by TEST_META_DATA_FILE,
 * not the normal metadata file, so should not be used for regression test purposes - these tests
 * are illustrative only and test functionality.
 *
 * @author Shaopeng Jia
 */
public class AsYouTypeFormatterTest extends TestCase {
  private PhoneNumberUtil phoneUtil;
  private static final String TEST_META_DATA_FILE =
      "/com/google/i18n/phonenumbers/PhoneNumberMetadataProtoForTesting";

  public AsYouTypeFormatterTest() {
    PhoneNumberUtil.resetInstance();
    InputStream in = PhoneNumberUtilTest.class.getResourceAsStream(TEST_META_DATA_FILE);
    phoneUtil = PhoneNumberUtil.getInstance(in);
  }

  public void testAYTFUS() {
    AsYouTypeFormatter formatter = phoneUtil.getAsYouTypeFormatter("US");
    assertEquals("6", formatter.inputDigit('6'));
    assertEquals("65", formatter.inputDigit('5'));
    assertEquals("650", formatter.inputDigit('0'));
    assertEquals("650 2", formatter.inputDigit('2'));
    assertEquals("650 25", formatter.inputDigit('5'));
    assertEquals("650 253", formatter.inputDigit('3'));
    // Note this is how a US local number (without area code) should be formatted.
    assertEquals("650 2532", formatter.inputDigit('2'));
    assertEquals("650 253 22", formatter.inputDigit('2'));
    assertEquals("650 253 222", formatter.inputDigit('2'));
    assertEquals("650 253 2222", formatter.inputDigit('2'));

    formatter.clear();
    assertEquals("1", formatter.inputDigit('1'));
    assertEquals("16", formatter.inputDigit('6'));
    assertEquals("1 65", formatter.inputDigit('5'));
    assertEquals("1 650", formatter.inputDigit('0'));
    assertEquals("1 650 2", formatter.inputDigit('2'));
    assertEquals("1 650 25", formatter.inputDigit('5'));
    assertEquals("1 650 253", formatter.inputDigit('3'));
    assertEquals("1 650 253 2", formatter.inputDigit('2'));
    assertEquals("1 650 253 22", formatter.inputDigit('2'));
    assertEquals("1 650 253 222", formatter.inputDigit('2'));
    assertEquals("1 650 253 2222", formatter.inputDigit('2'));

    formatter.clear();
    assertEquals("0", formatter.inputDigit('0'));
    assertEquals("01", formatter.inputDigit('1'));
    assertEquals("011 ", formatter.inputDigit('1'));
    assertEquals("011 4", formatter.inputDigit('4'));
    assertEquals("011 44 ", formatter.inputDigit('4'));
    assertEquals("011 44 6", formatter.inputDigit('6'));
    assertEquals("011 44 61", formatter.inputDigit('1'));
    assertEquals("011 44 6 12", formatter.inputDigit('2'));
    assertEquals("011 44 6 123", formatter.inputDigit('3'));
    assertEquals("011 44 6 123 1", formatter.inputDigit('1'));
    assertEquals("011 44 6 123 12", formatter.inputDigit('2'));
    assertEquals("011 44 6 123 123", formatter.inputDigit('3'));
    assertEquals("011 44 6 123 123 1", formatter.inputDigit('1'));
    assertEquals("011 44 6 123 123 12", formatter.inputDigit('2'));
    assertEquals("011 44 6 123 123 123", formatter.inputDigit('3'));

    formatter.clear();
    assertEquals("0", formatter.inputDigit('0'));
    assertEquals("01", formatter.inputDigit('1'));
    assertEquals("011 ", formatter.inputDigit('1'));
    assertEquals("011 5", formatter.inputDigit('5'));
    assertEquals("011 54 ", formatter.inputDigit('4'));
    assertEquals("011 54 9", formatter.inputDigit('9'));
    assertEquals("011 54 91", formatter.inputDigit('1'));
    assertEquals("011 54 9 11", formatter.inputDigit('1'));
    assertEquals("011 54 9 11 2", formatter.inputDigit('2'));
    assertEquals("011 54 9 11 23", formatter.inputDigit('3'));
    assertEquals("011 54 9 11 231", formatter.inputDigit('1'));
    assertEquals("011 54 9 11 2312", formatter.inputDigit('2'));
    assertEquals("011 54 9 11 2312 1", formatter.inputDigit('1'));
    assertEquals("011 54 9 11 2312 12", formatter.inputDigit('2'));
    assertEquals("011 54 9 11 2312 123", formatter.inputDigit('3'));
    assertEquals("011 54 9 11 2312 1234", formatter.inputDigit('4'));

    formatter.clear();
    assertEquals("0", formatter.inputDigit('0'));
    assertEquals("01", formatter.inputDigit('1'));
    assertEquals("011 ", formatter.inputDigit('1'));
    assertEquals("011 2", formatter.inputDigit('2'));
    assertEquals("011 24", formatter.inputDigit('4'));
    assertEquals("011 244 ", formatter.inputDigit('4'));
    assertEquals("011 244 2", formatter.inputDigit('2'));
    assertEquals("011 244 28", formatter.inputDigit('8'));
    assertEquals("011 244 280", formatter.inputDigit('0'));
    assertEquals("011 244 280 0", formatter.inputDigit('0'));
    assertEquals("011 244 280 00", formatter.inputDigit('0'));
    assertEquals("011 244 280 000", formatter.inputDigit('0'));
    assertEquals("011 244 280 000 0", formatter.inputDigit('0'));
    assertEquals("011 244 280 000 00", formatter.inputDigit('0'));
    assertEquals("011 244 280 000 000", formatter.inputDigit('0'));

    formatter.clear();
    assertEquals("+", formatter.inputDigit('+'));
    assertEquals("+4", formatter.inputDigit('4'));
    assertEquals("+48 ", formatter.inputDigit('8'));
    assertEquals("+48 8", formatter.inputDigit('8'));
    assertEquals("+48 88", formatter.inputDigit('8'));
    assertEquals("+48 88 1", formatter.inputDigit('1'));
    assertEquals("+48 88 12", formatter.inputDigit('2'));
    assertEquals("+48 88 123", formatter.inputDigit('3'));
    assertEquals("+48 88 123 1", formatter.inputDigit('1'));
    assertEquals("+48 88 123 12", formatter.inputDigit('2'));
    assertEquals("+48 88 123 12 1", formatter.inputDigit('1'));
    assertEquals("+48 88 123 12 12", formatter.inputDigit('2'));
  }

  public void testAYTFUSFullWidthCharacters() {
    AsYouTypeFormatter formatter = phoneUtil.getAsYouTypeFormatter("US");
    assertEquals("\uFF16", formatter.inputDigit('\uFF16'));
    assertEquals("\uFF16\uFF15", formatter.inputDigit('\uFF15'));
    assertEquals("650", formatter.inputDigit('\uFF10'));
    assertEquals("650 2", formatter.inputDigit('\uFF12'));
    assertEquals("650 25", formatter.inputDigit('\uFF15'));
    assertEquals("650 253", formatter.inputDigit('\uFF13'));
    assertEquals("650 2532", formatter.inputDigit('\uFF12'));
    assertEquals("650 253 22", formatter.inputDigit('\uFF12'));
    assertEquals("650 253 222", formatter.inputDigit('\uFF12'));
    assertEquals("650 253 2222", formatter.inputDigit('\uFF12'));
  }

  public void testAYTFUSMobileShortCode() {
    AsYouTypeFormatter formatter = phoneUtil.getAsYouTypeFormatter("US");
    assertEquals("*", formatter.inputDigit('*'));
    assertEquals("*1", formatter.inputDigit('1'));
    assertEquals("*12", formatter.inputDigit('2'));
    assertEquals("*121", formatter.inputDigit('1'));
    assertEquals("*121#", formatter.inputDigit('#'));
  }

  public void testAYTFUSVanityNumber() {
    AsYouTypeFormatter formatter = phoneUtil.getAsYouTypeFormatter("US");
    assertEquals("8", formatter.inputDigit('8'));
    assertEquals("80", formatter.inputDigit('0'));
    assertEquals("800", formatter.inputDigit('0'));
    assertEquals("800 ", formatter.inputDigit(' '));
    assertEquals("800 M", formatter.inputDigit('M'));
    assertEquals("800 MY", formatter.inputDigit('Y'));
    assertEquals("800 MY ", formatter.inputDigit(' '));
    assertEquals("800 MY A", formatter.inputDigit('A'));
    assertEquals("800 MY AP", formatter.inputDigit('P'));
    assertEquals("800 MY APP", formatter.inputDigit('P'));
    assertEquals("800 MY APPL", formatter.inputDigit('L'));
    assertEquals("800 MY APPLE", formatter.inputDigit('E'));
  }

  public void testAYTFAndRememberPositionUS() {
    AsYouTypeFormatter formatter = phoneUtil.getAsYouTypeFormatter("US");
    assertEquals("1", formatter.inputDigitAndRememberPosition('1'));
    assertEquals(1, formatter.getRememberedPosition());
    assertEquals("16", formatter.inputDigit('6'));
    assertEquals("1 65", formatter.inputDigit('5'));
    assertEquals(1, formatter.getRememberedPosition());
    assertEquals("1 650", formatter.inputDigitAndRememberPosition('0'));
    assertEquals(5, formatter.getRememberedPosition());
    assertEquals("1 650 2", formatter.inputDigit('2'));
    assertEquals("1 650 25", formatter.inputDigit('5'));
    // Note the remembered position for digit "0" changes from 4 to 5, because a space is now
    // inserted in the front.
    assertEquals(5, formatter.getRememberedPosition());
    assertEquals("1 650 253", formatter.inputDigit('3'));
    assertEquals("1 650 253 2", formatter.inputDigit('2'));
    assertEquals("1 650 253 22", formatter.inputDigit('2'));
    assertEquals(5, formatter.getRememberedPosition());
    assertEquals("1 650 253 222", formatter.inputDigitAndRememberPosition('2'));
    assertEquals(13, formatter.getRememberedPosition());
    assertEquals("1 650 253 2222", formatter.inputDigit('2'));
    assertEquals(13, formatter.getRememberedPosition());
    assertEquals("165025322222", formatter.inputDigit('2'));
    assertEquals(10, formatter.getRememberedPosition());
    assertEquals("1650253222222", formatter.inputDigit('2'));
    assertEquals(10, formatter.getRememberedPosition());

    formatter.clear();
    assertEquals("1", formatter.inputDigit('1'));
    assertEquals("16", formatter.inputDigitAndRememberPosition('6'));
    assertEquals(2, formatter.getRememberedPosition());
    assertEquals("1 65", formatter.inputDigit('5'));
    assertEquals("1 650", formatter.inputDigit('0'));
    assertEquals(3, formatter.getRememberedPosition());
    assertEquals("1 650 2", formatter.inputDigit('2'));
    assertEquals("1 650 25", formatter.inputDigit('5'));
    assertEquals(3, formatter.getRememberedPosition());
    assertEquals("1 650 253", formatter.inputDigit('3'));
    assertEquals("1 650 253 2", formatter.inputDigit('2'));
    assertEquals("1 650 253 22", formatter.inputDigit('2'));
    assertEquals(3, formatter.getRememberedPosition());
    assertEquals("1 650 253 222", formatter.inputDigit('2'));
    assertEquals("1 650 253 2222", formatter.inputDigit('2'));
    assertEquals("165025322222", formatter.inputDigit('2'));
    assertEquals(2, formatter.getRememberedPosition());
    assertEquals("1650253222222", formatter.inputDigit('2'));
    assertEquals(2, formatter.getRememberedPosition());

    formatter.clear();
    assertEquals("6", formatter.inputDigit('6'));
    assertEquals("65", formatter.inputDigit('5'));
    assertEquals("650", formatter.inputDigit('0'));
    assertEquals("650 2", formatter.inputDigit('2'));
    assertEquals("650 25", formatter.inputDigit('5'));
    assertEquals("650 253", formatter.inputDigit('3'));
    assertEquals("650 2532", formatter.inputDigitAndRememberPosition('2'));
    assertEquals(8, formatter.getRememberedPosition());
    assertEquals("650 253 22", formatter.inputDigit('2'));
    assertEquals(9, formatter.getRememberedPosition());
    assertEquals("650 253 222", formatter.inputDigit('2'));
    // No more formatting when semicolon is entered.
    assertEquals("650253222;", formatter.inputDigit(';'));
    assertEquals(7, formatter.getRememberedPosition());
    assertEquals("650253222;2", formatter.inputDigit('2'));

    formatter.clear();
    assertEquals("6", formatter.inputDigit('6'));
    assertEquals("65", formatter.inputDigit('5'));
    assertEquals("650", formatter.inputDigit('0'));
    // No more formatting when users choose to do their own formatting.
    assertEquals("650-", formatter.inputDigit('-'));
    assertEquals("650-2", formatter.inputDigitAndRememberPosition('2'));
    assertEquals(5, formatter.getRememberedPosition());
    assertEquals("650-25", formatter.inputDigit('5'));
    assertEquals(5, formatter.getRememberedPosition());
    assertEquals("650-253", formatter.inputDigit('3'));
    assertEquals(5, formatter.getRememberedPosition());
    assertEquals("650-253-", formatter.inputDigit('-'));
    assertEquals("650-253-2", formatter.inputDigit('2'));
    assertEquals("650-253-22", formatter.inputDigit('2'));
    assertEquals("650-253-222", formatter.inputDigit('2'));
    assertEquals("650-253-2222", formatter.inputDigit('2'));

    formatter.clear();
    assertEquals("0", formatter.inputDigit('0'));
    assertEquals("01", formatter.inputDigit('1'));
    assertEquals("011 ", formatter.inputDigit('1'));
    assertEquals("011 4", formatter.inputDigitAndRememberPosition('4'));
    assertEquals("011 48 ", formatter.inputDigit('8'));
    assertEquals(5, formatter.getRememberedPosition());
    assertEquals("011 48 8", formatter.inputDigit('8'));
    assertEquals(5, formatter.getRememberedPosition());
    assertEquals("011 48 88", formatter.inputDigit('8'));
    assertEquals("011 48 88 1", formatter.inputDigit('1'));
    assertEquals("011 48 88 12", formatter.inputDigit('2'));
    assertEquals(5, formatter.getRememberedPosition());
    assertEquals("011 48 88 123", formatter.inputDigit('3'));
    assertEquals("011 48 88 123 1", formatter.inputDigit('1'));
    assertEquals("011 48 88 123 12", formatter.inputDigit('2'));
    assertEquals("011 48 88 123 12 1", formatter.inputDigit('1'));
    assertEquals("011 48 88 123 12 12", formatter.inputDigit('2'));

    formatter.clear();
    assertEquals("+", formatter.inputDigit('+'));
    assertEquals("+1", formatter.inputDigit('1'));
    assertEquals("+1 6", formatter.inputDigitAndRememberPosition('6'));
    assertEquals("+1 65", formatter.inputDigit('5'));
    assertEquals("+1 650", formatter.inputDigit('0'));
    assertEquals(4, formatter.getRememberedPosition());
    assertEquals("+1 650 2", formatter.inputDigit('2'));
    assertEquals(4, formatter.getRememberedPosition());
    assertEquals("+1 650 25", formatter.inputDigit('5'));
    assertEquals("+1 650 253", formatter.inputDigitAndRememberPosition('3'));
    assertEquals("+1 650 253 2", formatter.inputDigit('2'));
    assertEquals("+1 650 253 22", formatter.inputDigit('2'));
    assertEquals("+1 650 253 222", formatter.inputDigit('2'));
    assertEquals(10, formatter.getRememberedPosition());

    formatter.clear();
    assertEquals("+", formatter.inputDigit('+'));
    assertEquals("+1", formatter.inputDigit('1'));
    assertEquals("+1 6", formatter.inputDigitAndRememberPosition('6'));
    assertEquals("+1 65", formatter.inputDigit('5'));
    assertEquals("+1 650", formatter.inputDigit('0'));
    assertEquals(4, formatter.getRememberedPosition());
    assertEquals("+1 650 2", formatter.inputDigit('2'));
    assertEquals(4, formatter.getRememberedPosition());
    assertEquals("+1 650 25", formatter.inputDigit('5'));
    assertEquals("+1 650 253", formatter.inputDigit('3'));
    assertEquals("+1 650 253 2", formatter.inputDigit('2'));
    assertEquals("+1 650 253 22", formatter.inputDigit('2'));
    assertEquals("+1 650 253 222", formatter.inputDigit('2'));
    assertEquals("+1650253222;", formatter.inputDigit(';'));
    assertEquals(3, formatter.getRememberedPosition());
  }

  public void testAYTFGBFixedLine() {
    AsYouTypeFormatter formatter = phoneUtil.getAsYouTypeFormatter("GB");
    assertEquals("0", formatter.inputDigit('0'));
    assertEquals("02", formatter.inputDigit('2'));
    assertEquals("020", formatter.inputDigit('0'));
    assertEquals("020 7", formatter.inputDigitAndRememberPosition('7'));
    assertEquals(5, formatter.getRememberedPosition());
    assertEquals("020 70", formatter.inputDigit('0'));
    assertEquals("020 703", formatter.inputDigit('3'));
    assertEquals(5, formatter.getRememberedPosition());
    assertEquals("020 7031", formatter.inputDigit('1'));
    assertEquals("020 7031 3", formatter.inputDigit('3'));
    assertEquals("020 7031 30", formatter.inputDigit('0'));
    assertEquals("020 7031 300", formatter.inputDigit('0'));
    assertEquals("020 7031 3000", formatter.inputDigit('0'));
  }

  public void testAYTFGBTollFree() {
     AsYouTypeFormatter formatter = phoneUtil.getAsYouTypeFormatter("gb");
    assertEquals("0", formatter.inputDigit('0'));
    assertEquals("08", formatter.inputDigit('8'));
    assertEquals("080", formatter.inputDigit('0'));
    assertEquals("080 7", formatter.inputDigit('7'));
    assertEquals("080 70", formatter.inputDigit('0'));
    assertEquals("080 703", formatter.inputDigit('3'));
    assertEquals("080 7031", formatter.inputDigit('1'));
    assertEquals("080 7031 3", formatter.inputDigit('3'));
    assertEquals("080 7031 30", formatter.inputDigit('0'));
    assertEquals("080 7031 300", formatter.inputDigit('0'));
    assertEquals("080 7031 3000", formatter.inputDigit('0'));
  }

  public void testAYTFGBPremiumRate() {
    AsYouTypeFormatter formatter = phoneUtil.getAsYouTypeFormatter("GB");
    assertEquals("0", formatter.inputDigit('0'));
    assertEquals("09", formatter.inputDigit('9'));
    assertEquals("090", formatter.inputDigit('0'));
    assertEquals("090 7", formatter.inputDigit('7'));
    assertEquals("090 70", formatter.inputDigit('0'));
    assertEquals("090 703", formatter.inputDigit('3'));
    assertEquals("090 7031", formatter.inputDigit('1'));
    assertEquals("090 7031 3", formatter.inputDigit('3'));
    assertEquals("090 7031 30", formatter.inputDigit('0'));
    assertEquals("090 7031 300", formatter.inputDigit('0'));
    assertEquals("090 7031 3000", formatter.inputDigit('0'));
  }

  public void testAYTFNZMobile() {
    AsYouTypeFormatter formatter = phoneUtil.getAsYouTypeFormatter("NZ");
    assertEquals("0", formatter.inputDigit('0'));
    assertEquals("02", formatter.inputDigit('2'));
    assertEquals("021", formatter.inputDigit('1'));
    assertEquals("02-11", formatter.inputDigit('1'));
    assertEquals("02-112", formatter.inputDigit('2'));
    // Note the unittest is using fake metadata which might produce non-ideal results.
    assertEquals("02-112 3", formatter.inputDigit('3'));
    assertEquals("02-112 34", formatter.inputDigit('4'));
    assertEquals("02-112 345", formatter.inputDigit('5'));
    assertEquals("02-112 3456", formatter.inputDigit('6'));
  }

  public void testAYTFDE() {
    AsYouTypeFormatter formatter = phoneUtil.getAsYouTypeFormatter("DE");
    assertEquals("0", formatter.inputDigit('0'));
    assertEquals("03", formatter.inputDigit('3'));
    assertEquals("030", formatter.inputDigit('0'));
    assertEquals("030 1", formatter.inputDigit('1'));
    assertEquals("030 12", formatter.inputDigit('2'));
    assertEquals("030 123", formatter.inputDigit('3'));
    assertEquals("030 1234", formatter.inputDigit('4'));

    // 08021 2345
    formatter.clear();
    assertEquals("0", formatter.inputDigit('0'));
    assertEquals("08", formatter.inputDigit('8'));
    assertEquals("080", formatter.inputDigit('0'));
    assertEquals("0802", formatter.inputDigit('2'));
    assertEquals("08021", formatter.inputDigit('1'));
    assertEquals("08021 2", formatter.inputDigit('2'));
    assertEquals("08021 23", formatter.inputDigit('3'));
    assertEquals("08021 234", formatter.inputDigit('4'));
    assertEquals("08021 2345", formatter.inputDigit('5'));

    // 00 1 650 253 2250
    formatter.clear();
    assertEquals("0", formatter.inputDigit('0'));
    assertEquals("00", formatter.inputDigit('0'));
    assertEquals("00 1 ", formatter.inputDigit('1'));
    assertEquals("00 1 6", formatter.inputDigit('6'));
    assertEquals("00 1 65", formatter.inputDigit('5'));
    assertEquals("00 1 650", formatter.inputDigit('0'));
    assertEquals("00 1 650 2", formatter.inputDigit('2'));
    assertEquals("00 1 650 25", formatter.inputDigit('5'));
    assertEquals("00 1 650 253", formatter.inputDigit('3'));
    assertEquals("00 1 650 253 2", formatter.inputDigit('2'));
    assertEquals("00 1 650 253 22", formatter.inputDigit('2'));
    assertEquals("00 1 650 253 222", formatter.inputDigit('2'));
    assertEquals("00 1 650 253 2222", formatter.inputDigit('2'));
  }

  public void testAYTFAR() {
    AsYouTypeFormatter formatter = phoneUtil.getAsYouTypeFormatter("AR");
    assertEquals("0", formatter.inputDigit('0'));
    assertEquals("01", formatter.inputDigit('1'));
    assertEquals("011", formatter.inputDigit('1'));
    assertEquals("011 7", formatter.inputDigit('7'));
    assertEquals("011 70", formatter.inputDigit('0'));
    assertEquals("011 703", formatter.inputDigit('3'));
    assertEquals("011 7031", formatter.inputDigit('1'));
    assertEquals("011 7031-3", formatter.inputDigit('3'));
    assertEquals("011 7031-30", formatter.inputDigit('0'));
    assertEquals("011 7031-300", formatter.inputDigit('0'));
    assertEquals("011 7031-3000", formatter.inputDigit('0'));
  }

  public void testAYTFARMobile() {
    AsYouTypeFormatter formatter = phoneUtil.getAsYouTypeFormatter("AR");
    assertEquals("+", formatter.inputDigit('+'));
    assertEquals("+5", formatter.inputDigit('5'));
    assertEquals("+54 ", formatter.inputDigit('4'));
    assertEquals("+54 9", formatter.inputDigit('9'));
    assertEquals("+54 91", formatter.inputDigit('1'));
    assertEquals("+54 9 11", formatter.inputDigit('1'));
    assertEquals("+54 9 11 2",
                 formatter.inputDigit('2'));
    assertEquals("+54 9 11 23", formatter.inputDigit('3'));
    assertEquals("+54 9 11 231", formatter.inputDigit('1'));
    assertEquals("+54 9 11 2312", formatter.inputDigit('2'));
    assertEquals("+54 9 11 2312 1", formatter.inputDigit('1'));
    assertEquals("+54 9 11 2312 12", formatter.inputDigit('2'));
    assertEquals("+54 9 11 2312 123", formatter.inputDigit('3'));
    assertEquals("+54 9 11 2312 1234", formatter.inputDigit('4'));
  }

  public void testAYTFKR() {
    // +82 51 234 5678
    AsYouTypeFormatter formatter = phoneUtil.getAsYouTypeFormatter("KR");
    assertEquals("+", formatter.inputDigit('+'));
    assertEquals("+8", formatter.inputDigit('8'));
    assertEquals("+82 ", formatter.inputDigit('2'));
    assertEquals("+82 5", formatter.inputDigit('5'));
    assertEquals("+82 51", formatter.inputDigit('1'));
    assertEquals("+82 51-2", formatter.inputDigit('2'));
    assertEquals("+82 51-23", formatter.inputDigit('3'));
    assertEquals("+82 51-234", formatter.inputDigit('4'));
    assertEquals("+82 51-234-5", formatter.inputDigit('5'));
    assertEquals("+82 51-234-56", formatter.inputDigit('6'));
    assertEquals("+82 51-234-567", formatter.inputDigit('7'));
    assertEquals("+82 51-234-5678", formatter.inputDigit('8'));

    // +82 2 531 5678
    formatter.clear();
    assertEquals("+", formatter.inputDigit('+'));
    assertEquals("+8", formatter.inputDigit('8'));
    assertEquals("+82 ", formatter.inputDigit('2'));
    assertEquals("+82 2", formatter.inputDigit('2'));
    assertEquals("+82 25", formatter.inputDigit('5'));
    assertEquals("+82 2-53", formatter.inputDigit('3'));
    assertEquals("+82 2-531", formatter.inputDigit('1'));
    assertEquals("+82 2-531-5", formatter.inputDigit('5'));
    assertEquals("+82 2-531-56", formatter.inputDigit('6'));
    assertEquals("+82 2-531-567", formatter.inputDigit('7'));
    assertEquals("+82 2-531-5678", formatter.inputDigit('8'));

    // +82 2 3665 5678
    formatter.clear();
    assertEquals("+", formatter.inputDigit('+'));
    assertEquals("+8", formatter.inputDigit('8'));
    assertEquals("+82 ", formatter.inputDigit('2'));
    assertEquals("+82 2", formatter.inputDigit('2'));
    assertEquals("+82 23", formatter.inputDigit('3'));
    assertEquals("+82 2-36", formatter.inputDigit('6'));
    assertEquals("+82 2-366", formatter.inputDigit('6'));
    assertEquals("+82 2-3665", formatter.inputDigit('5'));
    assertEquals("+82 2-3665-5", formatter.inputDigit('5'));
    assertEquals("+82 2-3665-56", formatter.inputDigit('6'));
    assertEquals("+82 2-3665-567", formatter.inputDigit('7'));
    assertEquals("+82 2-3665-5678", formatter.inputDigit('8'));

    // 02-114
    formatter.clear();
    assertEquals("0", formatter.inputDigit('0'));
    assertEquals("02", formatter.inputDigit('2'));
    assertEquals("021", formatter.inputDigit('1'));
    assertEquals("02-11", formatter.inputDigit('1'));
    assertEquals("02-114", formatter.inputDigit('4'));

    // 02-1300
    formatter.clear();
    assertEquals("0", formatter.inputDigit('0'));
    assertEquals("02", formatter.inputDigit('2'));
    assertEquals("021", formatter.inputDigit('1'));
    assertEquals("02-13", formatter.inputDigit('3'));
    assertEquals("02-130", formatter.inputDigit('0'));
    assertEquals("02-1300", formatter.inputDigit('0'));

    // 011-456-7890
    formatter.clear();
    assertEquals("0", formatter.inputDigit('0'));
    assertEquals("01", formatter.inputDigit('1'));
    assertEquals("011", formatter.inputDigit('1'));
    assertEquals("011-4", formatter.inputDigit('4'));
    assertEquals("011-45", formatter.inputDigit('5'));
    assertEquals("011-456", formatter.inputDigit('6'));
    assertEquals("011-456-7", formatter.inputDigit('7'));
    assertEquals("011-456-78", formatter.inputDigit('8'));
    assertEquals("011-456-789", formatter.inputDigit('9'));
    assertEquals("011-456-7890", formatter.inputDigit('0'));

    // 011-9876-7890
    formatter.clear();
    assertEquals("0", formatter.inputDigit('0'));
    assertEquals("01", formatter.inputDigit('1'));
    assertEquals("011", formatter.inputDigit('1'));
    assertEquals("011-9", formatter.inputDigit('9'));
    assertEquals("011-98", formatter.inputDigit('8'));
    assertEquals("011-987", formatter.inputDigit('7'));
    assertEquals("011-9876", formatter.inputDigit('6'));
    assertEquals("011-9876-7", formatter.inputDigit('7'));
    assertEquals("011-9876-78", formatter.inputDigit('8'));
    assertEquals("011-9876-789", formatter.inputDigit('9'));
    assertEquals("011-9876-7890", formatter.inputDigit('0'));
  }

  public void testAYTFMultipleLeadingDigitPatterns() {
    // +81 50 2345 6789
    AsYouTypeFormatter formatter = phoneUtil.getAsYouTypeFormatter("JP");
    assertEquals("+", formatter.inputDigit('+'));
    assertEquals("+8", formatter.inputDigit('8'));
    assertEquals("+81 ", formatter.inputDigit('1'));
    assertEquals("+81 5", formatter.inputDigit('5'));
    assertEquals("+81 50", formatter.inputDigit('0'));
    assertEquals("+81 50 2", formatter.inputDigit('2'));
    assertEquals("+81 50 23", formatter.inputDigit('3'));
    assertEquals("+81 50 234", formatter.inputDigit('4'));
    assertEquals("+81 50 2345", formatter.inputDigit('5'));
    assertEquals("+81 50 2345 6", formatter.inputDigit('6'));
    assertEquals("+81 50 2345 67", formatter.inputDigit('7'));
    assertEquals("+81 50 2345 678", formatter.inputDigit('8'));
    assertEquals("+81 50 2345 6789", formatter.inputDigit('9'));

    // +81 222 12 5678
    formatter.clear();
    assertEquals("+", formatter.inputDigit('+'));
    assertEquals("+8", formatter.inputDigit('8'));
    assertEquals("+81 ", formatter.inputDigit('1'));
    assertEquals("+81 2", formatter.inputDigit('2'));
    assertEquals("+81 22", formatter.inputDigit('2'));
    assertEquals("+81 22 2", formatter.inputDigit('2'));
    assertEquals("+81 22 21", formatter.inputDigit('1'));
    assertEquals("+81 2221 2", formatter.inputDigit('2'));
    assertEquals("+81 222 12 5", formatter.inputDigit('5'));
    assertEquals("+81 222 12 56", formatter.inputDigit('6'));
    assertEquals("+81 222 12 567", formatter.inputDigit('7'));
    assertEquals("+81 222 12 5678", formatter.inputDigit('8'));

    // +81 3332 2 5678
    formatter.clear();
    assertEquals("+", formatter.inputDigit('+'));
    assertEquals("+8", formatter.inputDigit('8'));
    assertEquals("+81 ", formatter.inputDigit('1'));
    assertEquals("+81 3", formatter.inputDigit('3'));
    assertEquals("+81 33", formatter.inputDigit('3'));
    assertEquals("+81 33 3", formatter.inputDigit('3'));
    assertEquals("+81 3332", formatter.inputDigit('2'));
    assertEquals("+81 3332 2", formatter.inputDigit('2'));
    assertEquals("+81 3332 2 5", formatter.inputDigit('5'));
    assertEquals("+81 3332 2 56", formatter.inputDigit('6'));
    assertEquals("+81 3332 2 567", formatter.inputDigit('7'));
    assertEquals("+81 3332 2 5678", formatter.inputDigit('8'));
  }
}
