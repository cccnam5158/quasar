/*
 * Copyright 2014–2017 SlamData Inc.
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

package quasar.fs

import quasar.Predef._
import quasar.{Data, DataCodec}
import quasar.Data._

import argonaut._, Argonaut._

import scalaz.Show

final case class WriteError(value: Data, hint: Option[String]) {
  def message = hint.getOrElse("error writing data") + "; value: " + Show[Data].shows(value)
}

object WriteError {
  implicit val Encode: EncodeJson[WriteError] = EncodeJson[WriteError](e =>
    Json("data"   := DataCodec.Precise.encode(e.value),
         "detail" := e.hint.getOrElse("")))

  implicit def writeErrorShow: Show[WriteError] =
    Show.shows(_.message)
}
