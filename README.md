PlayTags
===============

A Library to make play framework html helpers available for scalatags.
Use scalatags with the same functionality you have come to expect from play templates.

How To Use
===========
Add this to your dependencies -
```scala
"com.github.eklavya" %% "playtags" % "1.1"
```

Quick Example
=============
  ```scala
import java.util.Date  
import playtags.PlayTags._
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import play.twirl.api.{Html => H}

case class Test(name: String, password: String, age: Int, hobby: String, dob: Date, job: String, note: String, agree: Boolean)

  val testForm = Form[Test](
    mapping(
      "Name" -> nonEmptyText,
      "Password" -> nonEmptyText,
      "Age" -> number(14),
      "Hobby" -> nonEmptyText,
      "Date of Birth" -> date,
      "Job" -> nonEmptyText,
      "Note" -> text,
      "Agree to T&Cs" -> boolean
    )(Test.apply)(Test.unapply)
  )

  def testPage(tf: Form[Test]) = div(
    textarea(),
    pForm(routes.Application.test)(Seq(
      inputText(tf("Name"), Seq(cls := "myclass", id := "myNameId", "showConstraints".attr := "false")),
      inputPassword(tf("Password"), Seq(cls := "myclass", id := "myPassId")),
      inputText(tf("Age")),
      selectField(tf("Hobby"), Seq("S" -> "Swimming", "C" -> "Cycling"), Seq(cls := "myclass", id := "myHobbyId")),
      inputDate(tf("Date of Birth")),
      inputRadioGroup(tf("Job"), Seq("E" -> "Designer", "D" -> "Developer", "H" -> "Hacker")),
      textArea(tf("Note"), Seq(value := "Hello World!")),
      checkBox(tf("Agree to T&Cs")),
      input(`type` := "submit", method := "POST")
    ))
  )

  def index = Action {
    val res = H(Html(html(body(testPage(testForm)))))
    Ok(res)
  }

  def test = Action { implicit request =>
    testForm.bindFromRequest.fold(
      errors => {
        BadRequest(H(Html(
          html(
            body(
              testPage(errors))))))
      },
      t => Ok(H(Html(
        html(body
          (p("User Added"),
          testPage(testForm.fill(t)))))))
    )
  }
  ```
Helpers
=======
You have all the usual elements you have in play templates:

Convert your content to HTML to be sent to client. It adds <!DOCTYPE HTML> at the start.
```scala
def Html(tags: Modifier)
```

These are all the helpers.

```scala
textArea
selectField
inputText
inputRadioGroup
inputDate
inputFile
inputPassword
checkBox
pForm

requireJs
jsloader
javascriptRouter
```
As shown in the example above, they all take a ``` play.api.data.Field``` and an optional attributes sequence. Except the last three javascript helpers which behave exactly like they do in play templates.

Note
====
To turn showing constraints on or off, you can specify ```"showConstraints".attr := "false/true"```

License
=======
Apache 2.0

Copyright (c) 2015, Saurabh Rawat
