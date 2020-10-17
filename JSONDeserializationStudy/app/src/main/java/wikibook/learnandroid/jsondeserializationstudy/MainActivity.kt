package wikibook.learnandroid.jsondeserializationstudy

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

// 예제1) JSON 문자열 역직렬화
/*
data class MyJSONDataClass(val data1: Int, val data2: String, val list: List<Int>)
data class MyJSONNestedDataClass(val nested: Map<String, Any>)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var mapper = jacksonObjectMapper()

        val jsonString = """{ "data1": 1234, "data2": "Hello", "list": [1, 2, 3] }"""
        var d1 = mapper?.readValue<MyJSONDataClass>(jsonString)

        Log.d("mytag", "${d1.data1}")
        Log.d("mytag", "${d1.data2}")
        Log.d("mytag", "${d1.list}")

        val jsonString2 = """{ "nested": { "data1": 1234, "data2": "Hello", "list": [1, 2, 3] } }"""
        var d2 = mapper?.readValue<MyJSONNestedDataClass>(jsonString2)
        Log.d("mytag", "${d2.nested["data1"]}")
        Log.d("mytag", "${d2.nested["data2"]}")
        Log.d("mytag", "${d2.nested["list"]}")
    }
}
*/

// 예제2) 중첩 객체 역직렬화
/*
data class JSONData(val nested: JSONNested)
data class JSONNested(val data1: Int, val data2: String, val list: List<Int>)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var mapper = jacksonObjectMapper()

        val jsonString2 = """{ "nested": { "data1": 1234, "data2": "Hello", "list": [1, 2, 3] } }"""
        var d3 = mapper?.readValue<JSONData>(jsonString2)
        Log.d("mytag", "${d3.nested.data1}")
        Log.d("mytag", "${d3.nested.data2}")
        Log.d("mytag", "${d3.nested.list}")
    }
}
*/

// 예제3) 복잡한 중첩 객체들을 역직렬화
/*
data class ComplexJSONData(val nested: ComplexJSONNested)
data class ComplexJSONNested(@JsonProperty("inner_data") val innerData: String, @JsonProperty("inner_nested") val innerNested: ComplexJSONInnerNested)
data class ComplexJSONInnerNested(val data1: Int, val data2: String, val list: List<Int>)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var mapper = jacksonObjectMapper()

        val complexJsonString = """{ "nested": { "inner_data": "Hello from inner", "inner_nested": { "data1": 1234, "data2": "Hello", "list": [1, 2, 3] } } }"""
        var d4 = mapper?.readValue<ComplexJSONData>(complexJsonString)
        Log.d("mytag", "${d4.nested.innerData}")
        Log.d("mytag", "${d4.nested.innerNested.data1}")
        Log.d("mytag", "${d4.nested.innerNested.data2}")
        Log.d("mytag", "${d4.nested.innerNested.list}")
    }
}
*/

// 예제4) 역직렬화 작업을 수행할 코드를 직접 작성할 수 있도록 도와주는 StdDeserializer 클래스를 상속받아 역직렬화
@JsonDeserialize(using=MyComplexJSONDataDeserializer::class)
data class ComplexJSONData2(val innerData: String?, val data1: Int?, val data2: String?, val list: List<Int>?)

class MyComplexJSONDataDeserializer : StdDeserializer<ComplexJSONData2>(ComplexJSONData2::class.java) {
    override fun deserialize(parser: JsonParser?, ctx: DeserializationContext?): ComplexJSONData2 {
        val node : JsonNode? = parser?.codec?.readTree<JsonNode>(parser)

        val nestedNode : JsonNode? = node?.get("nested")
        val innerDataValue = nestedNode?.get("inner_data")?.asText()
        val innerNestedNode = nestedNode?.get("inner_nested")
        val innerNestedData1Node = innerNestedNode?.get("data1")?.asInt()
        val innerNestedData2Node = innerNestedNode?.get("data2")?.asText()

        val list = mutableListOf<Int>()
        innerNestedNode?.get("list")?.elements()?.forEach {
            list.add(it.asInt())
        }

        // (7)
        return ComplexJSONData2(innerDataValue, innerNestedData1Node, innerNestedData2Node, list)
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var mapper = jacksonObjectMapper()

        val complexJsonString = """{ "nested": { "inner_data": "Hello from inner", "inner_nested": { "data1": 1234, "data2": "Hello", "list": [1, 2, 3] } } }"""
        val d4 = ObjectMapper().readValue<ComplexJSONData2>(complexJsonString)
        Log.d("mytag", "${d4.innerData}")
        Log.d("mytag", "${d4.data1}")
        Log.d("mytag", "${d4.data2}")
        Log.d("mytag", "${d4.list}")
    }
}
