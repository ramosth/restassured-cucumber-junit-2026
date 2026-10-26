package com.test.automation.runners;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * Ponto de entrada da execução.
 *
 * O Surefire encontra esta classe pelo nome (padrão "Test*") e a entrega ao
 * JUnit Platform. As anotações dizem o que fazer:
 *   @Suite          -> esta classe é uma suíte (não tem testes próprios)
 *   @IncludeEngines -> quem executa é o motor do Cucumber
 *   @SelectPackages -> onde estão os .feature (pasta "features" do classpath)
 *
 * As configurações do Cucumber (glue, plugins, nomes) ficam em
 * src/test/resources/junit-platform.properties.
 *
 * Para rodar só uma parte dos cenários, use as tags do JUnit (sem o @):
 * mvn test "-Dgroups=smoke"
 * mvn test "-Dgroups=produtos & !expected-failure"
 */
@Suite
@IncludeEngines("cucumber")
@SelectPackages("features")
public class TestRunner {
    // Vazia de propósito: as anotações já fazem todo o trabalho
}