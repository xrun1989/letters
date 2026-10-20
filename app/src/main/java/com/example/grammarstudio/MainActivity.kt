package com.example.grammarstudio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Paper = Color(0xFFF1F0EA)
private val PaperBright = Color(0xFFF8F7F2)
private val Ink = Color(0xFF20201E)
private val Muted = Color(0xFF777770)
private val Rule = Color(0xFFCBCAC3)
private val RedMark = Color(0xFFC44B45)
private val OrangeMark = Color(0xFFB87835)
private val YellowMark = Color(0xFFB49A42)
private val BrownMark = Color(0xFF806A4B)

private val panelTabs = listOf("Sentence", "Craft & Style", "Authors", "Techniques", "Structure", "Plot", "Wiki-Lens")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GrammarStudioApp() }
    }
}

@Composable
private fun GrammarStudioApp() {
    var editor by remember {
        mutableStateOf(
            TextFieldValue("The city woke up in a way that felt different, as if it had been holding its breath.\n\nMara walked through the narrow streets, her coat pulled tight, not because of the cold, but because she didn’t trust the people who were watching from the corners.\n\nThe old writer were searching for a better ending.")
        )
    }
    var selectedWord by remember { mutableStateOf("were") }
    var selectedTab by remember { mutableStateOf("Sentence") }
    var lens by remember { mutableStateOf("Grammar") }
    var screenwritingMode by remember { mutableStateOf("Professional") }
    var grammarStrength by remember { mutableStateOf("Strong") }
    var americanEnglish by remember { mutableStateOf(true) }
    var showMarks by remember { mutableStateOf(true) }
    var showWordStrip by remember { mutableStateOf(true) }
    var showBottom by remember { mutableStateOf(true) }
    var expandedBottom by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    val word = selectedWord.ifBlank { "Select a word" }
    val analysis = analyzeWord(word, editor.text)
    val bottomHeight = if (expandedBottom) 236.dp else 142.dp

    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color = Paper) {
            Column(Modifier.fillMaxSize()) {
                TopBar(
                    lens = lens,
                    onLens = { lens = if (lens == "Grammar") "Screenwriting" else "Grammar" },
                    onSettings = { showSettings = true },
                    onToggleBottom = { showBottom = !showBottom }
                )
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    // On a phone the navigation remains compact; it never gets covered by the analysis desk.
                    Column(
                        Modifier.width(88.dp).fillMaxHeight()
                            .border(width = 0.5.dp, color = Rule)
                            .padding(horizontal = 7.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("MODULES", fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = Muted)
                        NavItem("Screen", lens == "Screenwriting") { lens = "Screenwriting" }
                        NavItem("Grammar", lens == "Grammar") { lens = "Grammar" }
                        HorizontalDivider(color = Rule)
                        NavItem("Notes", false) { }
                        NavItem("Dictionary", false) { }
                        Spacer(Modifier.weight(1f))
                        NavItem("Settings", false) { showSettings = true }
                    }
                    Column(Modifier.weight(1f).fillMaxHeight()) {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("The Last Night", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Ink)
                                Text(if (lens == "Grammar") "Grammar Lens · $grammarStrength" else "Screenwriting Lens · $screenwritingMode", fontSize = 10.sp, color = Muted)
                            }
                            Text("Aa  ◐", fontSize = 12.sp, color = Muted, modifier = Modifier.clickable { showSettings = true }.padding(4.dp))
                        }
                        Box(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
                            BasicTextField(
                                value = editor,
                                onValueChange = { newValue ->
                                    editor = newValue
                                    selectedWord = wordAtCursor(newValue.text, newValue.selection.start)
                                },
                                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                                textStyle = TextStyle(
                                    color = Ink,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 16.sp,
                                    lineHeight = 27.sp
                                ),
                                decorationBox = { inner ->
                                    Column {
                                        Text("UNTITLED NOTE", fontSize = 8.sp, color = Muted, letterSpacing = 1.5.sp)
                                        Spacer(Modifier.height(10.dp))
                                        inner()
                                    }
                                }
                            )
                            if (showWordStrip && selectedWord.isNotBlank()) {
                                WordStrip(
                                    selectedWord = selectedWord,
                                    analysis = analysis,
                                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 34.dp)
                                )
                            }
                        }
                    }
                }
                if (showBottom) {
                    BottomAnalysisDesk(
                        editorText = editor.text,
                        selectedWord = selectedWord,
                        selectedTab = selectedTab,
                        onTab = { selectedTab = it },
                        onWord = { selectedWord = it },
                        expanded = expandedBottom,
                        onExpand = { expandedBottom = !expandedBottom },
                        onHide = { showBottom = false },
                        modifier = Modifier.fillMaxWidth().height(bottomHeight)
                    )
                }
                Row(
                    Modifier.fillMaxWidth().background(Ink).padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${editor.text.trim().split(Regex("\\s+")).count { it.isNotBlank() }} words", color = PaperBright, fontSize = 9.sp)
                    Text("$lens Lens · ${if (americanEnglish) "American" else "British"} English", color = PaperBright, fontSize = 9.sp)
                }
            }
        }
    }

    if (showSettings) {
        AlertDialog(
            onDismissRequest = { showSettings = false },
            containerColor = PaperBright,
            title = { Text("Lens settings", color = Ink) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Module", color = Muted, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Grammar", "Screenwriting").forEach { option ->
                            FilterChip(selected = lens == option, onClick = { lens = option }, label = { Text(option, fontSize = 10.sp) })
                        }
                    }
                    if (lens == "Grammar") {
                        Text("Grammar rigidity", color = Muted, fontSize = 11.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("Strong", "Common", "Informal").forEach {
                                FilterChip(selected = grammarStrength == it, onClick = { grammarStrength = it }, label = { Text(it, fontSize = 10.sp) })
                            }
                        }
                    } else {
                        Text("Writing style", color = Muted, fontSize = 11.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("Professional", "Editor", "Common").forEach {
                                FilterChip(selected = screenwritingMode == it, onClick = { screenwritingMode = it }, label = { Text(it, fontSize = 10.sp) })
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("American English", color = Ink, fontSize = 12.sp)
                        Switch(checked = americanEnglish, onCheckedChange = { americanEnglish = it })
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Show word strip", color = Ink, fontSize = 12.sp)
                        Switch(checked = showWordStrip, onCheckedChange = { showWordStrip = it })
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Show manuscript marks", color = Ink, fontSize = 12.sp)
                        Switch(checked = showMarks, onCheckedChange = { showMarks = it })
                    }
                    Text("Marking palette: red error · orange suspicious · yellow warning · brown regional usage. Halftone-only marking will be added with the theme controls.", fontSize = 10.sp, color = Muted)
                }
            },
            confirmButton = { TextButton(onClick = { showSettings = false }) { Text("Done") } }
        )
    }
}

@Composable
private fun TopBar(lens: String, onLens: () -> Unit, onSettings: () -> Unit, onToggleBottom: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().height(44.dp).background(PaperBright)
            .border(width = 0.5.dp, color = Rule).padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("G", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Ink)
        Text("Grammar Studio", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Ink)
        Spacer(Modifier.weight(1f))
        Text(lens, modifier = Modifier.clickable { onLens() }.padding(5.dp), color = Ink, fontSize = 10.sp)
        Text("▤", modifier = Modifier.clickable { onToggleBottom() }.padding(5.dp), color = Ink, fontSize = 15.sp)
        Text("⚙", modifier = Modifier.clickable { onSettings() }.padding(5.dp), color = Ink, fontSize = 15.sp)
    }
}

@Composable
private fun NavItem(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        label,
        modifier = Modifier.fillMaxWidth().background(if (selected) Color(0xFFE0DFD8) else Color.Transparent, RoundedCornerShape(3.dp))
            .clickable { onClick() }.padding(horizontal = 4.dp, vertical = 6.dp),
        color = Ink,
        fontSize = 10.sp,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
    )
}

private data class WordAnalysis(val category: String, val explanation: String, val suggestion: String, val isError: Boolean = false)

@Composable
private fun WordStrip(selectedWord: String, analysis: WordAnalysis, modifier: Modifier = Modifier) {
    Column(
        modifier.background(Color(0xFFF0EFE9)).border(1.dp, Color(0xFF888780), RoundedCornerShape(2.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp).widthIn(max = 285.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("${analysis.category} · $selectedWord", color = Ink, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            Text("×", color = Muted, fontSize = 10.sp)
        }
        Text(analysis.explanation, color = Ink, fontSize = 9.sp, lineHeight = 12.sp, maxLines = 3)
        if (analysis.suggestion.isNotBlank()) {
            Text("→ ${analysis.suggestion}", color = Ink, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun BottomAnalysisDesk(
    editorText: String,
    selectedWord: String,
    selectedTab: String,
    onTab: (String) -> Unit,
    onWord: (String) -> Unit,
    expanded: Boolean,
    onExpand: () -> Unit,
    onHide: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.background(PaperBright).border(width = 1.dp, color = Ink)
    ) {
        Row(
            Modifier.fillMaxWidth().height(32.dp).background(Color(0xFFE5E4DE)).horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            panelTabs.forEach { tab ->
                Column(
                    Modifier.clickable { onTab(tab) }.padding(horizontal = 9.dp, vertical = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(tab, color = Ink, fontSize = 9.sp, fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal)
                    if (selectedTab == tab) Spacer(Modifier.fillMaxWidth().height(1.dp).background(Ink))
                }
            }
            Text(if (expanded) "−" else "+", modifier = Modifier.clickable { onExpand() }.padding(7.dp), color = Ink, fontSize = 12.sp)
            Text("×", modifier = Modifier.clickable { onHide() }.padding(7.dp), color = Ink, fontSize = 12.sp)
        }
        when (selectedTab) {
            "Sentence" -> SentenceTab(editorText, selectedWord, onWord)
            "Craft & Style" -> SimpleTab(
                "CRAFT & STYLE",
                "Rhythm · clarity · tension · voice",
                "Inspect sentence rhythm, vague phrasing, repetition, point of view, and narrative tension. Each finding should open a deeper explanation rather than merely label the text."
            )
            "Authors" -> SimpleTab(
                "STYLE REFERENCES",
                "Published craft sources · not authorship detection",
                "Explore cited techniques associated with writers and editors. Similarity estimates are provisional and must not be presented as proof that a passage imitates or was written by an author."
            )
            "Techniques" -> SimpleTab(
                "TECHNIQUE INDEX",
                "Pacing · subtext · atmosphere · dialogue · point of view",
                "Tap a technique to open its definition, examples, counterexamples, and source references inside this same panel."
            )
            "Structure" -> SimpleTab("STRUCTURE", "Scene · beat · paragraph · chapter", "Inspect the selected sentence in its paragraph and scene context. Later versions can map setup, turn, consequence, and continuity.")
            "Plot" -> SimpleTab("PLOT NOTES", "Setup · clue · tension · payoff", "Track narrative function and possible clues across the manuscript. Any plot interpretation must remain a suggestion for the writer.")
            else -> SimpleTab("WIKI-LENS", "Searchable craft reference", "The same panel can navigate from a word to a technique, then to a detailed article and its cited source, without opening a separate screen.")
        }
    }
}

@Composable
private fun SentenceTab(text: String, selectedWord: String, onWord: (String) -> Unit) {
    val sentence = text.split(Regex("(?<=[.!?])\\s+|\\n+")).firstOrNull { it.contains(selectedWord, ignoreCase = true) } ?: text.lineSequence().firstOrNull().orEmpty()
    Column(Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 5.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("FULL SENTENCE STUDY", color = Muted, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically) {
            Regex("[A-Za-z]+(?:['’-][A-Za-z]+)*|[^A-Za-z\\s]+").findAll(sentence).forEach { match ->
                val token = match.value
                val lower = token.lowercase()
                val mark = when {
                    lower == "were" && sentence.contains("writer", true) -> RedMark
                    lower in listOf("different", "thing", "very", "really") -> OrangeMark
                    lower == "just" -> YellowMark
                    else -> Color.Transparent
                }
                Text(
                    token,
                    modifier = Modifier.clickable { onWord(token) }.padding(end = 3.dp)
                        .then(if (mark != Color.Transparent) Modifier.border(width = 0.dp, color = mark) else Modifier),
                    color = Ink,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Serif
                )
                if (mark != Color.Transparent) {
                    Box(Modifier.padding(end = 3.dp).width(10.dp).height(2.dp).background(mark))
                }
            }
        }
        HorizontalDivider(color = Rule, thickness = 0.5.dp)
        Text(
            when {
                selectedWord.equals("were", true) && sentence.contains("writer", true) -> "ERROR · “Were” does not agree with singular “writer.” Suggested correction: “was.”"
                else -> "Selected: “$selectedWord” · Choose a marked phrase to inspect its role, effect, and alternatives."
            },
            color = Ink,
            fontSize = 9.sp,
            lineHeight = 12.sp,
            maxLines = 2
        )
    }
}

@Composable
private fun SimpleTab(title: String, subtitle: String, body: String) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 9.dp, vertical = 7.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(title, color = Ink, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Text(subtitle, color = Muted, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
        Text(body, color = Ink, fontSize = 9.sp, lineHeight = 12.sp)
    }
}

private fun wordAtCursor(text: String, cursor: Int): String {
    if (text.isBlank()) return ""
    val safe = cursor.coerceIn(0, text.length)
    val regex = Regex("[A-Za-z]+(?:['’-][A-Za-z]+)*")
    return regex.findAll(text).firstOrNull { safe in it.range || safe == it.range.last + 1 }?.value
        ?: regex.findAll(text).lastOrNull { it.range.last < safe }?.value.orEmpty()
}

private fun analyzeWord(word: String, text: String): WordAnalysis {
    val w = word.lowercase()
    val singularWere = Regex("\\b(the old writer|writer|he|she|it)\\s+were\\b", RegexOption.IGNORE_CASE)
    if (w == "were" && singularWere.containsMatchIn(text)) {
        return WordAnalysis(
            "VERB · AGREEMENT",
            "The subject is singular, so the verb form does not agree in this clause.",
            "Replace “were” with “was”.",
            true
        )
    }
    if (w in listOf("was", "were", "is", "are", "has", "have", "does", "do")) {
        return WordAnalysis("VERB · CHECK AGREEMENT", "Check the subject, tense, and meaning of the complete clause.", "Inspect the surrounding sentence.")
    }
    if (w.endsWith("ly") && w.length > 3) {
        return WordAnalysis("LIKELY ADVERB", "An “-ly” ending often marks an adverb, but context determines its role.", "Check whether it adds useful precision.")
    }
    if (w in listOf("a", "an", "the")) {
        return WordAnalysis("ARTICLE", "Articles help identify whether a noun is general or specific.", "Check the following sound and the reader's context.")
    }
    if (w.isBlank()) return WordAnalysis("WORD INSPECTOR", "Place the cursor in a word to inspect it.", "")
    return WordAnalysis("WORD INSPECTOR", "“$word” is selected. Detailed dictionary and part-of-speech data are not connected yet.", "Inspect context, meaning, and sentence function.")
}
