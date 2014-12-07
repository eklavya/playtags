package playtags

import play.api.data.Field
import play.api.mvc.Call
import scalatags.Text.all._
import scalatags.generic.AttrPair
import scalatags.text.Builder

object PlayTags {

  def Html(tags: Modifier) = "<!DOCTYPE html>" + tags.toString

  def textArea(f: Field, attrs: Seq[AttrPair[Builder, String]] = Seq()) = {
    genInput(f, attrs) { (iyd, nme, valu, attr) =>
      textarea(id := iyd, name := nme, attr)
    }
  }

  def selectField(f: Field, options: Seq[(String, String)], attrs: Seq[AttrPair[Builder, String]] = Seq()) = {
    genInput(f, attrs) { (iyd, nme, valu, attr) =>

      val selectName = if (attr.exists(_.a.name == "multiple")) s"$nme[]" else nme

      val values = f.indexes.nonEmpty && attr.exists(_.a.name == "multiple") match {
        case true => f.indexes.map(i => f(s"[$i]").value).flatten.toSet
        case _ => f.value.toSet
      }

      val opts = options.map { v =>
        if (values.contains(v._1)) {
          option(value := v._1, "selected".attr := "selected")(v._2)
        } else {
          option(value := v._1)(v._2)
        }
      }

      select(id := iyd,
        name := selectName,
        attr,
        attr.find(_.a.name == "default").map { defaultValue =>
          option(cls := "blank", value := "")(defaultValue)
        },
        opts)
    }
  }

  def requireJs(module: String, core: String, productionFolderPrefix: String = "-min", folder: String = "javascripts") = {
    script(`type` := "text/javascript",
      "data-main".attr := {
        if (play.api.Mode.Prod == play.api.Play.maybeApplication.map(_.mode).getOrElse(play.api.Mode.Dev))
          module.replace(folder, folder + productionFolderPrefix)
        else
          module
      },
      src := core)
  }

  val jsloader = raw(
    """
        |<script type="text/javascript">
        |var require = function(moduleName) {
          |  var body = "";
          |  $.ajax({
            |    url: "/assets/javascripts/" + moduleName + ".js",
            |    dataType: "text", async: false,
            |    success: function(result) { body = result; }
            |  });
            |  body = "var exports = {};\n" + body + "\nreturn exports;";
            |  var fnct = new Function("module", "exports", body);
            |  return fnct();
            |}
            |</script>
            """.stripMargin)

  def javascriptRouter(name: String = "Router")(routes: play.core.Router.JavascriptReverseRoute*)(implicit request: play.api.mvc.RequestHeader) = {
    script(`type` := "text/javascript")(raw(Html(play.api.Routes.javascriptRouter(name)(routes: _*).body.replace("/", "\\/")).toString()))
  }

  def inputText(f: Field, attrs: Seq[AttrPair[Builder, String]] = Seq()) = {
    genInput(f, attrs) { (iyd, nme, valu, attr) =>
      val inputType = attrs.find(_.a.name == "type").map(_.v.toString).getOrElse("text")
      input(`type` := inputType,
        id := iyd,
        name := nme,
        value := valu.getOrElse(""),
        attr)
    }
  }

  def inputRadioGroup(f: Field, options: Seq[(String, String)], attrs: Seq[AttrPair[Builder, String]] = Seq()) = {
    genInput(f, attrs) { (iyd, nme, valu, attr) =>
      span(cls := "buttonset", id := iyd)(
        options.map { v =>
          val chked = if (valu == Some(v._1)) "checked" else ""
          input(`type` := "radio",
            id := s"${iyd}_${v._1}",
            name := nme,
            value := v._1,
            checked := chked)(label(`for` := s"${iyd}_${v._1}")(v._2))
        })
    }
  }

  def inputDate(f: Field, attrs: Seq[AttrPair[Builder, String]] = Seq()) = {
    genInput(f, attrs) {
      (iyd, nme, valu, attr) =>
        input(`type` := "date",
          id := iyd,
          name := nme,
          value := valu.getOrElse(""),
          attr)
    }
  }

  def inputFile(f: Field, attrs: Seq[AttrPair[Builder, String]] = Seq()) = {
    genInput(f, attrs) { (iyd, nme, valu, attr) =>
      input(`type` := "file",
        id := iyd,
        name := nme,
        attr)
    }
  }

  def inputPassword(f: Field, attrs: Seq[AttrPair[Builder, String]] = Seq()) = {
    genInput(f, attrs) {
      (iyd, nme, valu, attr) =>
        input(`type` := "password",
          id := iyd,
          name := nme,
          attr)
    }
  }

  private def genInput(f: Field, attrs: Seq[AttrPair[Builder, String]])(fun: (String, String, Option[String], Seq[AttrPair[Builder, String]]) => Modifier) = {

    val iyd = attrs.find(_.a.name == "id").map(_.v).getOrElse(f.id)

    fieldConstructor(iyd,
      f,
      fun(iyd,
        f.name,
        f.value,
        attrs.filterNot(ap =>
          ap.a.name == "class" ||
            ap.a.name == "id" ||
            ap.a.name == "showConstraints" ||
            ap.a.name == "name" ||
            ap.a.name == "label")),
      attrs)
  }

  private def fieldConstructor(iyd: String, f: Field, inner: Modifier, attrs: Seq[AttrPair[Builder, String]]) = {
    val clss = attrs.find(_.a.name == "class").map(_.v).getOrElse("") + {
      if (f.hasErrors) " error" else ""
    }
    val lbl = attrs.find(_.a.name == "label").getOrElse(play.api.i18n.Messages(f.label)).toString
    val showConstraints = attrs.find(_.a.name == "showConstraints").map(_.v == "true").getOrElse(true)

    dl(cls := clss,
      id := iyd,
      dt(label(`for` := iyd)(lbl)),
      dd(inner),
      f.errors.map(e => play.api.i18n.Messages(e.message, e.args: _*)).map(dd(cls := "error")(_)),
      (if (showConstraints) {
        f.constraints.map(c => play.api.i18n.Messages(c._1, c._2: _*)) ++
          f.format.map(f => play.api.i18n.Messages(f._1, f._2: _*))
      } else Nil).map(dd(cls := "info")(_)))
  }

  def checkBox(f: Field, attrs: Seq[AttrPair[Builder, String]] = Seq()) = {
    genInput(f, attrs) { (iyd, nme, valu, attr) =>
      val boxValue = attrs.find(_.a.name == "value").map(_.v).getOrElse("true")
      val chked = if (valu == Some(boxValue)) "checked" else ""

      input(`type` := "checkbox",
        id := iyd,
        name := nme,
        value := boxValue,
        checked := chked,
        attr.filterNot(_.a.name == "value"))(span(
          attrs.find(_.a.name == "text").getOrElse("")))
    }
  }

  def pForm(a: Call, attrs: Seq[AttrPair[Builder, String]] = Seq())(body: Seq[Modifier]) = {
    form(action := a.url, method := a.method, attrs)(body)
  }

  def inputCheckBoxGroup(f: Field, options: Seq[(String, String)], attrs: Seq[AttrPair[Builder, String]] = Seq()) = {
    genInput(f, attrs) { (iyd, nme, valu, attr) =>
      val values = f.indexes.map(i => f(s"[$i]").value).flatten.toSet

      span(cls := "buttonset",
        id := iyd)(
          options.map { v =>
            val chked = if (values.contains(v._1)) "checked" else ""
            Seq(input(`type` := "checkbox",
              id := s"${iyd}_${v._1}",
              name := s"$nme[]",
              value := v._1,
              checked := chked),
              label(`for` := s"${iyd}_${v._1}")(v._2))
          })
    }
  }
}
