package fkt
import fkt.facets.IndexingFramePolicy
import fkt.facets.TextualCoupler
import fkt.facets.TogglingCoupler
import fkt.facets.TTarget
import fkt.SelectingTitles as Titles
import fkt.SimpleTitles as Simples
class TextContent(var text: String) {
	override fun toString() = text
	override fun equals(other: Any?) =
					other != null && text == (other as TextContent).text

	fun clone() = TextContent(text)
	fun copyClone(clone: TextContent) {
		this.text = clone.text
	}

  override fun hashCode(): Int {
    return text.hashCode()
  }
}

open class SelectingSurface(test: TargetTest,trace:Boolean)
	: SurfaceCore(trace,test) {
	protected val list = mutableListOf(
					TextContent("Hello world!"),
					TextContent("Hello Dolly!"),
					TextContent("Hello, good evening and welcome!"))

	override fun getContentTrees(): List<TTarget> {
		val appTitle = TargetTest.Selecting.toString()
		return listOf(facets.newIndexingFrame(object : IndexingFramePolicy() {
			override val frameTitle = appTitle
			override val indexingTitle = Titles.Select
			override val getIndexables = { list }
			override val newUiSelectable = { indexable: Any -> (indexable as TextContent).text }
			override val newFrameTargets = {
				listOf(
								facets.newTextualTarget(Simples.Indexed, object : TextualCoupler() {
									override val getText = { _: String ->
										val indexed = facets.getIndexingState(Titles.Select).indexed as TextContent
										SelectableType.getContentType(indexed).title
									}
								}),
								facets.newTogglingTarget(Titles.Live, object : TogglingCoupler() {
									override val passSet = true
								})
				)
			}
			override val newIndexedTreeTitle = { indexed: Any ->
				appTitle + SelectableType.getContentType(indexed as TextContent).titleTail
			}
			override val newIndexedTree = { indexed: Any, indexedTreeTitle: String ->
				val content = indexed as TextContent
				val type = SelectableType.getContentType(content)
				val tail = type.titleTail
				facets.newTargetGroup(indexedTreeTitle,
								if (type == SelectableType.Standard)
									listOf(newEditTarget(content, tail))
								else listOf(newEditTarget(content, tail), newCharsTarget(tail))
				)
			}
		}))
	}

	private fun getIndexedType(): SelectableType {
		val content = facets.getIndexingState(
						Titles.Select).indexed as TextContent
		return SelectableType.getContentType(content)
	}

	override fun doTraceMsg(msg: String) {
		if (false || facets.doTrace) super.doTraceMsg(msg)
	}

	override fun buildSurface() {
		super.buildSurface()
		if (false) return
		val add = {
			list.add(TextContent("Hello sailor!"))
			trace(" > Simulating input: update=", list[list.size - 1].text)
			facets.notifyTargetUpdated(Titles.Select)
		}
		val edit = {
			val update = "Hello !"
			trace(" > Simulating input: update=", update)
			val title = Titles.EditText
			facets.updateTargetWithNotify(title, update)
		}
		val select = {
			val update = 2
			trace(" > Simulating input: update=", update)
			val title = Titles.Select
			facets.updateTargetWithNotify(title, update)
		}
		for (update in listOf(add, edit, select)) update()
	}

	override fun onRetargeted(activeTitle: String) {
		val live = (facets.getTargetState(Titles.Live)?:true) as Boolean
		val type = getIndexedType()
		val tail = type.titleTail
		facets.setTargetLive(
						Titles.EditText + tail,
						live)
		if (type == SelectableType.ShowChars)
			 facets.setTargetLive(
							 Titles.CharsCount + tail,
							 live)
	}
	override fun buildLayout() {
		generateFacets(
						Titles.Select,
						Titles.EditText)
	}
	protected fun newEditTarget(indexed: TextContent, tail: String): TTarget =
		facets.newTextualTarget(
						Titles.EditText + tail,
						object : TextualCoupler() {
			override val passText = indexed.text
			override val targetStateUpdated = {
				state: Any, _: String ->
				indexed.text = state as String
			}
		}
		)

	protected fun newCharsTarget(tail: String): TTarget =
		facets.newTextualTarget(Titles.CharsCount + tail, object : TextualCoupler() {
			override val getText = { _: String ->
				"" + (facets.getTargetState(Titles.EditText + Titles.CharsTail) as String).length
			}
		})
	}
