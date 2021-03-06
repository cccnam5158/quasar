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

package quasar.common

import quasar.Predef._
import quasar.{NonTerminal, RenderedTree, RenderTree, Terminal}, RenderTree.ops._

import argonaut._, Argonaut._
import scalaz.Show
import scalaz.syntax.show._

sealed trait PhaseResult {
  def name: String
}

object PhaseResult {
  final case class Tree(name: String, value: RenderedTree) extends PhaseResult
  final case class Detail(name: String, value: String)     extends PhaseResult

  def tree[A: RenderTree](name: String, value: A): PhaseResult =
    Tree(name, value.render)
  def detail(name: String, value: String): PhaseResult = Detail(name, value)

  implicit def show: Show[PhaseResult] = Show.shows {
    case Tree(name, value)   => name + ":\n" + value.shows
    case Detail(name, value) => name + ":\n" + value
  }

  implicit def renderTree: RenderTree[PhaseResult] = new RenderTree[PhaseResult] {
    def render(v: PhaseResult) = v match {
      case Tree(name, value)   => NonTerminal(List("PhaseResult"), Some(name), List(value))
      case Detail(name, value) => NonTerminal(List("PhaseResult"), Some(name), List(Terminal(List("Detail"), Some(value))))
    }
  }

  implicit def phaseResultEncodeJson: EncodeJson[PhaseResult] = EncodeJson {
    case Tree(name, value)   => Json.obj("name" := name, "tree" := value)
    case Detail(name, value) => Json.obj("name" := name, "detail" := value)
  }
}
