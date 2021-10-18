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
package io.micronaut.security.token.paseto.generator;

import dev.paseto.jpaseto.PasetoBuilder;
import dev.paseto.jpaseto.Pasetos;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.security.token.paseto.config.PrivateKeyConfiguration;
import jakarta.inject.Singleton;

/**
 * Implementation of {@link PasetoBuilderGenerator} for Paseto Purpose {@link dev.paseto.jpaseto.Purpose#PUBLIC}.
 * @author Sergio del Amo
 * @since 3.2.0
 */
@Requires(beans = PrivateKeyConfiguration.class)
@Singleton
public class PublicPasetoBuilderGenerator implements PasetoBuilderGenerator {
    private final PrivateKeyConfiguration configuration;

    public PublicPasetoBuilderGenerator(PrivateKeyConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    @NonNull
    public PasetoBuilder<?> builder() {
        switch (configuration.getVersion()) {
            case V2:
                return Pasetos.V2.PUBLIC.builder().setPrivateKey(configuration.getPrivateKey());
            case V1:
            default:
                return Pasetos.V1.PUBLIC.builder().setPrivateKey(configuration.getPrivateKey());
        }
    }
}