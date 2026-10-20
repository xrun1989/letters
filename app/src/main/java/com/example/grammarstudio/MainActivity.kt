package com.example.grammarstudio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Paper = Color(0xFFF3EAD5)
private val PaperLight = Color(0xFFFAF6EC)
private val Ink = Color(0xFF292923)
private val MutedInk = Color(0xFF716D62)
private val Rule = Color(0xFFD4C9B2)
private val HighlightYellow = Color(0xFFF4D66D)
private val HighlightPink = Color(0xFFF1B6C4)
private val HighlightBlue = Color(0xFFB8D7E8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GrammarStudioApp() }
    }
}

@Composable
private fun GrammarStudioApp() {
    var draft by remember {
        mutableStateOf("The old writer were walking through a quiet street, searching for the right words.")
    }
    var selectedWord by remember { mutableStateOf("were") }
    val words = remember(draft) {
        Regex("[A-Za-z]+(?:['’-][A-Za-z]+)*")
            .findAll(draft)
            .map { it.value }
            .distinctBy { it.lowercase() }
            .take(36)
            .toList()
    }
    val analysis = analyzeWord(selectedWord, draft)

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Paper) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "GRAMMAR STUDIO",
                            color = Ink,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.4.sp
                        )
                        Text("A writer's desk · v0.1", color = MutedInk, fontSize = 12.sp)
                    }
                    Text(
                        "EN",
                        modifier = Modifier
                            .border(1.dp, Rule, RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        color = Ink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    LegendPill("Grammar", HighlightPink)
                    LegendPill("Word choice", HighlightYellow)
                    LegendPill("Structure", HighlightBlue)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PaperLight, RoundedCornerShape(8.dp))
                        .border(1.dp, Rule, RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    Text("UNTITLED NOTE", color = MutedInk, fontSize = 10.sp, letterSpacing = 1.5.sp)
                    Spacer(Modifier.height(10.dp))
                    BasicTextField(
                        value = draft,
                        onValueChange = { draft = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = Ink,
                            fontFamily = FontFamily.Serif,
                            fontSize = 18.sp,
                            lineHeight = 29.sp
                        ),
                        decorationBox = { innerTextField ->
                            Column {
                                innerTextField()
                                if (draft.isBlank()) {
                                    Text("Begin writing here…", color = MutedInk, fontFamily = FontFamily.Serif)
                                }
                            }
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "${draft.trim().split(Regex("\\s+")).count { it.isNotBlank() }} words",
                        color = MutedInk,
                        fontSize = 11.sp
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEAE0C9), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("WORD DESK", color = Ink, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.2.sp)
                    Text("Select a word to inspect it", color = MutedInk, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        words.forEach { word ->
                            val active = word.equals(selectedWord, ignoreCase = true)
                            val color = when {
                                active -> HighlightYellow
                                word.lowercase() in listOf("were", "was", "is", "are", "has", "have", "do", "does") -> HighlightPink
                                word.endsWith("ly", true) -> HighlightBlue
                                else -> PaperLight
                            }
                            Text(
                                word,
                                modifier = Modifier
                                    .background(color, RoundedCornerShape(5.dp))
                                    .border(1.dp, if (active) Ink else Rule, RoundedCornerShape(5.dp))
                                    .clickable { selectedWord = word }
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                color = Ink,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF292923), RoundedCornerShape(8.dp))
                        .padding(15.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("EDITOR'S NOTES", color = Color(0xFFF4D66D), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(" / ", color = Color(0xFF8D887B))
                        Text(selectedWord.ifBlank { "Select a word" }, color = Color(0xFFF7F0DF), fontFamily = FontFamily.Serif, fontSize = 18.sp)
                    }
                    Text(analysis.category, color = Color(0xFFB8D7E8), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text(analysis.explanation, color = Color(0xFFF3EAD5), fontSize = 14.sp, lineHeight = 21.sp)
                    if (analysis.suggestion.isNotBlank()) {
                        Text("POSSIBLE REVISION", color = Color(0xFFF4D66D), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text(analysis.suggestion, color = Color.White, fontFamily = FontFamily.Serif, fontSize = 15.sp)
                    }
                    Text(
                        "Prototype note: explanations use local rules, not a full grammar engine.",
                        color = Color(0xFFBDB6A7),
                        fontSize = 10.sp
                    )
                }

                Text(
                    "FILE   Edit   View   Dictionary   Settings",
                    modifier = Modifier.padding(top = 2.dp),
                    color = MutedInk,
                    fontSize = 11.sp,
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}

@Composable
private fun LegendPill(label: String, tint: Color) {
    Row(
        modifier = Modifier
            .background(PaperLight, RoundedCornerShape(4.dp))
            .border(1.dp, Rule, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(7.dp).height(7.dp).background(tint, RoundedCornerShape(2.dp)))
        Spacer(Modifier.width(5.dp))
        Text(label, color = Ink, fontSize = 10.sp)
    }
}

private data class WordAnalysis(
    val category: String,
    val explanation: String,
    val suggestion: String
)

private fun analyzeWord(word: String, draft: String): WordAnalysis {
    val w = word.lowercase()
    val sentence = draft.trim()
    val mismatch = Regex("\\b(the old writer|he|she|it)\\s+were\\b", RegexOption.IGNORE_CASE)
    if (w == "were" && mismatch.containsMatchIn(sentence)) {
        return WordAnalysis(
            "GRAMMAR · SUBJECT–VERB AGREEMENT",
            "“Writer” is singular, so the verb should agree with it. “Were” is normally used with plural subjects and with “you.”",
            sentence.replace(mismatch, "$1 was")
        )
    }
    if (w in listOf("was", "were", "is", "are", "has", "have", "does", "do")) {
        return WordAnalysis(
            "GRAMMAR · VERB",
            "This is a verb form. Check whether it agrees with the subject and whether the sentence's time is consistent.",
            "Read the whole clause with its subject, then choose the matching verb form."
        )
    }
    if (w.endsWith("ly") && w.length > 3) {
        return WordAnalysis(
            "WORD CLASS · LIKELY ADVERB",
            "Words ending in “-ly” are often adverbs, which can describe how an action happens. The ending is a clue, not a guarantee.",
            "Check whether this word modifies a verb, adjective, or another adverb."
        )
    }
    if (w in listOf("a", "an", "the")) {
        return WordAnalysis(
            "WORD CLASS · ARTICLE",
            "Articles introduce nouns. Use “a” before a consonant sound, “an” before a vowel sound, and “the” when the reader can identify the thing meant.",
            "Check the sound that follows “a/an” and whether the noun is specific."
        )
    }
    if (w.isBlank()) {
        return WordAnalysis("WORD INSPECTOR", "Tap a word in the word desk to inspect it.", "")
    }
    return WordAnalysis(
        "WORD INSPECTOR · GENERAL",
        "“$word” appears in your draft. In this prototype, detailed dictionary definitions and dependable part-of-speech tagging are not yet connected.",
        "Read the word in its sentence context; its grammatical role may change with context."
    )
}
