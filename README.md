PlayTags
===============

A Library to make play framework html helpers available for scalatags.
Use scalatags with the same functionality you have come to expect from play templates.
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
License
=======
Apache 2.0

Copyright (c) 2014, Saurabh Rawat
