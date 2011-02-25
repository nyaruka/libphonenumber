// Copyright (C) 2011 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// Author: Philippe Liard

#include <cassert>
#include <cstring>
#include <sstream>

#include "stringutil.h"

namespace i18n {
namespace phonenumbers {

using std::stringstream;

string operator+(const string& s, int n) {
  stringstream ss;

  ss << s << n;
  string result;
  ss >> result;

  return result;
}

string SimpleItoa(int n) {
  stringstream ss;

  ss << n;
  string result;
  ss >> result;

  return result;
}

bool TryStripPrefixString(const string& in, const string& prefix, string* out) {
  assert(out);
  const bool has_prefix = in.compare(0, prefix.length(), prefix) == 0;
  out->assign(has_prefix ? in.substr(prefix.length()) : in);

  return has_prefix;
}

bool HasSuffixString(const string& s, const string& suffix) {
  if (s.length() < suffix.length()) {
    return false;
  }
  return s.compare(s.length() - suffix.length(), suffix.length(), suffix) == 0;
}

// StringHolder class

StringHolder::StringHolder(const string& s) :
  string_(&s),
  cstring_(NULL),
  len_(s.size())
{}

StringHolder::StringHolder(const char* s) :
  string_(NULL),
  cstring_(s),
  len_(std::strlen(s))
{}

StringHolder::~StringHolder() {}

// StrCat

// Implement s += sh; (s: string, sh: StringHolder)
string& operator+=(string& lhs, const StringHolder& rhs) {
  const string* const s = rhs.GetString();
  if (s) {
    lhs += *s;
  } else {
    const char* const cs = rhs.GetCString();
    if (cs)
      lhs.append(cs, rhs.Length());
  }
  return lhs;
}

string StrCat(const StringHolder& s1, const StringHolder& s2) {
  string result;
  result.reserve(s1.Length() + s2.Length() + 1);

  result += s1;
  result += s2;

  return result;
}

string StrCat(const StringHolder& s1, const StringHolder& s2,
              const StringHolder& s3) {
  string result;
  result.reserve(s1.Length() + s2.Length() + s3.Length() + 1);

  result += s1;
  result += s2;
  result += s3;

  return result;
}

string StrCat(const StringHolder& s1, const StringHolder& s2,
              const StringHolder& s3, const StringHolder& s4) {
  string result;
  result.reserve(s1.Length() + s2.Length() + s3.Length() + s4.Length() + 1);

  result += s1;
  result += s2;
  result += s3;
  result += s4;

  return result;
}

string StrCat(const StringHolder& s1, const StringHolder& s2,
              const StringHolder& s3, const StringHolder& s4,
              const StringHolder& s5) {
  string result;
  result.reserve(s1.Length() + s2.Length() + s3.Length() + s4.Length() +
                 s5.Length() + 1);
  result += s1;
  result += s2;
  result += s3;
  result += s4;
  result += s5;

  return result;
}

string StrCat(const StringHolder& s1, const StringHolder& s2,
              const StringHolder& s3, const StringHolder& s4,
              const StringHolder& s5, const StringHolder& s6) {
  string result;
  result.reserve(s1.Length() + s2.Length() + s3.Length() + s4.Length() +
                 s5.Length() + s6.Length() + 1);
  result += s1;
  result += s2;
  result += s3;
  result += s4;
  result += s5;
  result += s6;

  return result;
}

string StrCat(const StringHolder& s1, const StringHolder& s2,
              const StringHolder& s3, const StringHolder& s4,
              const StringHolder& s5, const StringHolder& s6,
              const StringHolder& s7, const StringHolder& s8,
              const StringHolder& s9, const StringHolder& s10,
              const StringHolder& s11) {
  string result;
  result.reserve(s1.Length() + s2.Length()  + s3.Length() + s4.Length() +
                 s5.Length() + s6.Length()  + s7.Length() + s8.Length() +
                 s9.Length() + s10.Length() + s11.Length());
  result += s1;
  result += s2;
  result += s3;
  result += s4;
  result += s5;
  result += s6;
  result += s7;
  result += s8;
  result += s9;
  result += s10;
  result += s11;

  return result;
}

}  // namespace phonenumbers
}  // namespace i18n
