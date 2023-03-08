/*
 * Copyright 2023 HM Revenue & Customs
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

package views.newBehaviours

import org.jsoup.Jsoup
import play.twirl.api.HtmlFormat
import views.NewViewSpecBase

trait ViewBehaviours extends NewViewSpecBase {

  def normalPage(view: HtmlFormat.Appendable,
    messageKeyPrefix: String,
    expectedGuidanceKeys: String*): Unit = {

    "behave like a normal page" when {

      "rendered" must {

        "have the correct banner title" in {

          val doc = asDocument(view)
          assertRenderedByCssSelector(doc, "div.govuk-header__content")
        }

        "hide account menu when user not logged in" in {

          val doc = asDocument(view)
          assertNotRenderedById(doc, "secondary-nav")
        }

        "display the correct browser title" in {

          val doc = asDocument(view)
          assertEqualsMessage(
            doc,
            "title",
            s"${messages(s"$messageKeyPrefix.title")} - ${frontendAppConfig.serviceTitle}"
          )
        }

        "display the correct page title" in {

          val doc = asDocument(view)
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading")
        }

        "display the correct guidance" in {

          val doc = asDocument(view)
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }

        "display language toggles" in {

          val doc = asDocument(view)
          assertRenderedByCssSelector(doc, "nav.hmrc-language-select")
        }
      }
    }
  }

  def pageWithAccountMenu(view: HtmlFormat.Appendable): Unit = {

    "behave like a normal page with account menu" when {

      "rendered" must {

        "show account menu when user logged in" in {

          val doc = asDocument(view)
          assertRenderedById(doc, "secondary-nav")
        }

        "have the sign-out option rendered" in {

          val doc = asDocument(view)
          doc.select("ul.hmrc-account-menu__main > li:nth-child(5)").text() mustBe "Sign out"
        }
      }
    }
  }

  def pageWithBackLink(view: HtmlFormat.Appendable): Unit = {

    "behave like a page with a back link" must {

      "have a back link" in {

        val doc = asDocument(view)
        assertRenderedById(doc, "back-link")
      }
    }
  }

  def pageWithList(view: HtmlFormat.Appendable,
                   pageKey: String,
                   bulletList: Seq[String]): Unit = {

    "behave like a page with a list" must {

      "have a list" in {

        val doc = asDocument(view)
        bulletList.foreach {
          x => assertRenderedById(doc, s"bullet-$x")
        }
      }

      "have correct values" in {

        val doc = asDocument(view)
        bulletList.foreach {
          x => assertContainsMessages(doc, s"$pageKey.$x")
        }
      }
    }
  }

  def pageWithSecondaryHeader(view: HtmlFormat.Appendable,
                              heading: String): Unit = {

    "behave like a page with a secondary header" in {

      Jsoup.parse(view.toString()).getElementsByClass("heading-secondary").text() must include(heading)
    }
  }

  def pageWithButtonLink(view: HtmlFormat.Appendable,
                         url: String,
                         id: String): Unit = {

    "behave like a page with a button link" must {

      "have a button" in {

        val doc = asDocument(view)
        assertRenderedById(doc, messages(id).toLowerCase)
      }

      "have a url" in {

        val doc = asDocument(view)
        val result = doc.getElementById(messages(id).toLowerCase)
        result.attr("href") mustBe url
      }
    }
  }

  def pageWithBodyText(view: HtmlFormat.Appendable,
                       messageKey: String*): Unit = {

    "behave like a page with body text" must {

      "display content" in {
        val doc = asDocument(view)
        for (key <- messageKey) {
          assertContainsMessages(doc, key)
        }
      }
    }
  }

  def pageWithHyperLink(view: HtmlFormat.Appendable,
                        url: String,
                        id: String = "link"): Unit = {

    "behave like a page with a url link" must {
      "display link" in {
        val doc = asDocument(view)
        doc.getElementById(id).attr("href") mustBe url
      }
    }
  }

}

