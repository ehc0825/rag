package kr.co.ehc0104.rag.ollama.generate;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = OllamaGenerateOption.Builder.class)
public class OllamaGenerateOption {

    private Float temperature;
    private Integer topK;
    private Float minP;
    private Float topP;

    protected OllamaGenerateOption() {}

    public Float getTemperature() {return temperature;}

    public Integer getTopK() {
        return topK;
    }

    public Float getMinP() {
        return minP;
    }

    public Float getTopP() {return topP;}

    /**
     * ollama generate request option builder
     * @return ollama generate option builder
     */
    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private Float temperature;
        private Integer topK;
        private Float minP;
        private Float topP;

        private Builder(){};

        public Builder temperature(float temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder topK(int topK) {
            this.topK = topK;
            return this;
        }

        public Builder minP(float minP) {
            this.minP = minP;
            return this;
        }

        public Builder topP(float topP) {
            this.topP = topP;
            return this;
        }


        /**
         * ollama generate option 빌드
         * @return OllamaGenerateOption
         */
        public OllamaGenerateOption build() {
            OllamaGenerateOption ollamaGenerateOption = new OllamaGenerateOption();
            if (temperature != null) {
                ollamaGenerateOption.temperature=temperature;
            }
            if (topK != null) {
                ollamaGenerateOption.topK=topK;
            }
            if (minP != null) {
                ollamaGenerateOption.minP = minP;
            }
            if (topP != null) {
                ollamaGenerateOption.topP=topP;
            }
            return ollamaGenerateOption;
        }

    }


}
