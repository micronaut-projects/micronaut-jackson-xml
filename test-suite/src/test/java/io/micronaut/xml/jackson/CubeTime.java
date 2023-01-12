/*
 * Copyright 2017-2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.xml.jackson;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Introspected
public class CubeTime {
    @NonNull
    @NotBlank
    @JacksonXmlProperty(isAttribute = true)
    private String time;

    @NonNull
    @NotNull
    @JacksonXmlProperty(localName = "Cube")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<@Valid CubeRate> rates;

    public CubeTime() {
    }

    public CubeTime(@NonNull String time,
                    @NonNull List<@Valid CubeRate> rates) {
        this.time = time;
        this.rates = rates;
    }

    public void setTime(@NonNull String time) {
        this.time = time;
    }

    public void setRates(@NonNull List<CubeRate> rates) {
        this.rates = rates;
    }

    @NonNull
    public String getTime() {
        return time;
    }

    @NonNull
    public List<CubeRate> getRates() {
        return rates;
    }

    @Override
    public String toString() {
        return "<Cube time='" + time + "'>" + rates.stream().map(CubeRate::toString).collect(Collectors.joining()) + "</Cube>";
    }
}
