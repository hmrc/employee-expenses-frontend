/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

	 import base.SpecBase
	 import org.joda.time.LocalDate
	 import play.api.libs.json.Json

	 class TaxCodeRecordSpec extends SpecBase {
		 "Taxcode Record " must {
			 "return a taxCodeModel when passed valid JSON" in {

				 val taxRecord = validJson.as[Seq[TaxCodeRecord]]

				 taxRecord mustBe Seq(TaxCodeRecord(
					 taxCode = "830L",
					 employerName = "Employer Name",
					 startDate = LocalDate.parse("2018-06-27"),
					 endDate = LocalDate.parse("2019-04-05"),
					 payrollNumber = Some("1"),
					 pensionIndicator = true,
					 primary = true
				 ))
			 }
		 }

		 val validJson = Json.parse("""{
																	|  "data" : {
																	|    "current": [{
																	|      "taxCode": "830L",
																	|      "employerName": "Employer Name",
																	|      "operatedTaxCode": true,
																	|      "p2Issued": true,
																	|      "startDate": "2018-06-27",
																	|      "endDate": "2019-04-05",
																	|      "payrollNumber": "1",
																	|      "pensionIndicator": true,
																	|      "primary": true
																	|    }],
																	|    "previous": [{
																	|      "taxCode": "1150L",
																	|      "employerName": "Employer Name",
																	|      "operatedTaxCode": true,
																	|      "p2Issued": true,
																	|      "startDate": "2018-04-06",
																	|      "endDate": "2018-06-26",
																	|      "payrollNumber": "1",
																	|      "pensionIndicator": true,
																	|      "primary": true
																	|    }]
																	|  },
																	|  "links" : [ ]
																	|}""".stripMargin)
	 }
