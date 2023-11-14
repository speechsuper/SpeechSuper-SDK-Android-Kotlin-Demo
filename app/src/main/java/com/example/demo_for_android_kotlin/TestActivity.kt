package com.example.demo_for_android_kotlin

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class TestActivity : AppCompatActivity() {

    private var isRecording: Boolean = false
    private var coreType = ""

    private val mTextResult: TextView by lazy {
        findViewById(R.id.mTextResult)
    }

    private val mToolbar: Toolbar by lazy {
        findViewById(R.id.mToolbar)
    }

    private val mBtnEval: Button by lazy {
        findViewById(R.id.mBtn_eval)
    }

    private val mRefText: EditText by lazy {
        findViewById(R.id.mRefText)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)


        val bundle = this.intent.extras

        coreType = bundle!!.getString("coreType").toString()

        mToolbar.setNavigationIcon(R.drawable.back)
        mToolbar.setNavigationOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    finish()
                }
            }
        )
        if (coreType == "word.eval") {
            mRefText.setText("hello")
        }

        if (coreType == "sent.eval") {
            mRefText.setText("Welcome to beijing.")
        }

        if (coreType == "para.eval") {
            mRefText.setText("My friend Lucy often gets up at seven o’clock in the morning. After breakfast she goes to school at half past seven. The lessons start at eight. She has four lessons in the morning and two in the afternoon. She has lunch at school. After school she often plays table tennis with me. In the evening she watches TV, then she does her homework. She goes to bed at half past ten.")
        }

        if (coreType == "word.eval.cn") {
            mRefText.setText("海")
        }

        if (coreType == "sent.eval.cn") {
            mRefText.setText("你好")
        }

        if (coreType == "para.eval.cn") {
            mRefText.setText("两个黄鹂鸣翠柳， 一行白鹭上青天。 窗含西岭千秋雪， 门泊东吴万里船")
        }

        mBtnEval.setOnClickListener {
            doBtnEval()
        }
    }

    fun setResult(response: String) {
        val responseObj = JSONObject(response)
        if(responseObj.has("vad_status")) { // omit vad_status
            return
        }
        var textResult = StringBuilder()
        if (responseObj.has("error")) {
            this.runOnUiThread {
                isRecording = false
                mBtnEval.setText(R.string.startEval)
            }
        } else {
            if (responseObj.has("result")) {
                val resultObj = responseObj.getJSONObject("result")
                val overall= resultObj.get("overall")
                textResult.append("overall: " + overall +"\n")

                if (coreType == "word.eval" || coreType == "word.eval.cn") {
                    textResult.append("Phoneme score:/")
                    val wjsono: JSONArray =  resultObj.getJSONArray("words")
                    val wjson = wjsono.getJSONObject(0).getJSONArray("phonemes")
                    for (i in 0 until wjson.length()) {
                        textResult.append(
                            wjson.getJSONObject(i).getString("phoneme") + ":" + wjson.getJSONObject(i)
                                .getString("pronunciation") + " /"
                        )
                    }
                    textResult.append("\n")
                    if (responseObj.has("stress")) {
                        var stress = true
                        textResult.append("Is the rereading correct:")
                        val stressAll: String = responseObj.getString("stress")
                        if ("0" == stressAll) {
                            stress = false
                        }
                        textResult.append(if (stress) "Correct stress syllables" else "Incorrect stressed syllables")
                        textResult.append("\n")
                    }
                    if (wjsono.getJSONObject(0).getJSONObject("scores").has("stress")) {
                        val stressJson = wjsono.getJSONObject(0).getJSONObject("scores").getJSONArray("stress")
                        val stressStr = java.lang.StringBuilder("Details of stressed syllables: ")
                        for (j in 0 until stressJson.length()) {
                            var refStressStr = ""
                            var realStressStr = ""
                            val refStress = stressJson.getJSONObject(j).getInt("ref_stress")
                            val realStress = stressJson.getJSONObject(j).getInt("stress")
                            refStressStr = if (refStress.equals("1")) {
                                "stressed syllable"
                            } else if (refStress.equals("2")) {
                                "Hypobaric syllable"
                            } else {
                                "Unstressed reading"
                            }
                            realStressStr = if (refStress == realStress) {
                                "(correct)"
                            } else {
                                "(error)"
                            }
                            stressStr.append(
                                "/" + stressJson.getJSONObject(j).getString("phonetic") + "/" + " "
                            )
                            stressStr.append(refStressStr)
                            stressStr.append("$realStressStr ")
                        }
                        textResult.append(stressStr)
                    }
                }
                else if (coreType == "sent.eval" || coreType == "sent.eval.cn") {

                    if (resultObj.has("pronunciation")) {
                        textResult.append(
                            """
                        pronunciation score:${resultObj.getString("pronunciation")}
                        """.trimIndent()
                        )
                    }
                    if (resultObj.has("integrity")) {
                        textResult.append(
                            """
                        integrity: ${resultObj.getString("integrity")}
                        """.trimIndent()
                        )
                    }
                    if (resultObj.has("fluency")) {
                        textResult.append(
                            """
                        fluency: ${resultObj.getString("fluency")}
                        """.trimIndent()
                        )
                    }
                    if (resultObj.has("rhythm")) {
                        textResult.append(
                            """
                        rhythm:${resultObj.getString("rhythm")}
                        """.trimIndent()
                        )
                    }
                    textResult.append("Word Score:\n")
                    val wjsono: JSONArray = resultObj.getJSONArray("words")
                    for (i in 0 until wjsono.length()) {
                        var word = wjsono.getJSONObject(i).getString("word")
                            .replace("\\.|\\,|\\!|\\;|\\?|\"".toRegex(), "")
                        if (word.startsWith("\'") || word.endsWith("\'")) {
                            word = word.replace("\'", "")
                        }
                        textResult.append("$word: ")
                        textResult.append(
                            wjsono.getJSONObject(i).getJSONObject("scores").getString("overall") + " "
                        )
                    }
                    textResult.append("\n")
                }
                else if (coreType == "para.eval" || coreType == "para.eval.cn") {
                    if (resultObj.has("pronunciation")) {
                        textResult.append(
                            """
                        pronunciation score:${resultObj.getString("pronunciation")}
                        """.trimIndent()
                        )
                    }
                    if (resultObj.has("integrity")) {
                        textResult.append(
                            """
                        integrity score: ${resultObj.getString("integrity")}
                        """.trimIndent()
                        )
                    }
                    if (resultObj.has("fluency")) {
                        textResult.append(
                            """
                        fluency score: ${resultObj.getString("fluency")}
                        """.trimIndent()
                        )
                    }
                    if (resultObj.has("rhythm")) {
                        textResult.append(
                            """
                        rhythm score:${resultObj.getString("rhythm")}
                        """.trimIndent()
                        )
                    }
                }
            }
        }
        textResult.append("\n")
        textResult.append("\n")
        textResult.append(responseObj.toString(4))
        this.runOnUiThread{
            mTextResult.text = textResult.toString()
        }
    }

    fun doBtnEval() {
        if(isRecording) { //stop record
            isRecording = false
            SkegnManager.getInstance(this).stopSkegn()
            mBtnEval.setText(R.string.startEval)
        } else { //start record
            isRecording = true
            mBtnEval.setText(R.string.stopEval)
            mTextResult.text = ""
            val refText = mRefText.text.toString()

            val requestObj = JSONObject()
            requestObj.put("coreType", coreType)
            requestObj.put("refText", refText)

            SkegnManager.getInstance(this).startSkegn(requestObj, object: SkegnManager.CallbackResult{
                override fun run(response: String){
                    setResult(response)
                }
            });
        }
    }
}
