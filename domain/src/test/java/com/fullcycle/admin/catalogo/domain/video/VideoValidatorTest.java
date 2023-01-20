package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.UnitTest;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.Set;

public class VideoValidatorTest extends UnitTest {

    @Test
    public void givenNullTitle_whenCallsValidate_shouldReceiveError() {
        // given
        final String expectedTitle = null;
        final var expectedDescription = """
                Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                Para acessar todas as aulas, lives e desafios, acesse:
                https://imersao.fullcycle.com.br/
                """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'title' should not be null";

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedMembers
        );

        // when
        final var actualError = Assertions.assertThrows(
                DomainException.class,
                () -> actualVideo.validate(new ThrowsValidationHandler())
        );

        // then
        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }

    @Test
    public void givenEmptyTitle_whenCallsValidate_shouldReceiveError() {
        // given
        final var expectedTitle = "";
        final var expectedDescription = """
                Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                Para acessar todas as aulas, lives e desafios, acesse:
                https://imersao.fullcycle.com.br/
                """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'title' should not be empty";

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedMembers
        );

        // when
        final var actualError = Assertions.assertThrows(
                DomainException.class,
                () -> actualVideo.validate(new ThrowsValidationHandler())
        );

        // then
        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }

    @Test
    public void givenTitleWithLengthGreaterThan255_whenCallsValidate_shouldReceiveError() {
        // given
        final var expectedTitle = """
                Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                Para acessar todas as aulas, lives e desafios, acesse:
                https://imersao.fullcycle.com.br/
                """;
        final var expectedDescription = """
                Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                Para acessar todas as aulas, lives e desafios, acesse:
                https://imersao.fullcycle.com.br/
                """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'title' must be between 1 and 255 characters";

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedMembers
        );

        // when
        final var actualError = Assertions.assertThrows(
                DomainException.class,
                () -> actualVideo.validate(new ThrowsValidationHandler())
        );

        // then
        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }

    @Test
    public void givenEmptyDescription_whenCallsValidate_shouldReceiveError() {
        // given
        final var expectedTitle = "System Design Interviews";
        final var expectedDescription = "";
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be empty";

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedMembers
        );

        // when
        final var actualError = Assertions.assertThrows(
                DomainException.class,
                () -> actualVideo.validate(new ThrowsValidationHandler())
        );

        // then
        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }

    @Test
    public void givenDescriptionWithLengthGreaterThan4000_whenCallsValidate_shouldReceiveError() {
        // given
        final var expectedTitle = "System Design Interviews";
        final var expectedDescription = """
                A prática cotidiana prova que o surgimento do comércio virtual não pode mais se dissociar das posturas dos órgãos dirigentes com relação às suas atribuições. As experiências acumuladas demonstram que o aumento do diálogo entre os diferentes setores produtivos cumpre um papel essencial na formulação das diretrizes de desenvolvimento para o futuro. Assim mesmo, o acompanhamento das preferências de consumo obstaculiza a apreciação da importância do investimento em reciclagem técnica. Por conseguinte, o julgamento imparcial das eventualidades auxilia a preparação e a composição dos paradigmas corporativos. Do mesmo modo, a consulta aos diversos militantes garante a contribuição de um grupo importante na determinação da gestão inovadora da qual fazemos parte.
                                    
                Caros amigos, o desenvolvimento contínuo de distintas formas de atuação prepara-nos para enfrentar situações atípicas decorrentes das direções preferenciais no sentido do progresso. Não obstante, a contínua expansão de nossa atividade facilita a criação do retorno esperado a longo prazo. No mundo atual, a mobilidade dos capitais internacionais maximiza as possibilidades por conta dos relacionamentos verticais entre as hierarquias.
                        
                Por outro lado, a competitividade nas transações comerciais deve passar por modificações independentemente dos índices pretendidos. A nível organizacional, a estrutura atual da organização talvez venha a ressaltar a relatividade do sistema de participação geral. Evidentemente, a percepção das dificuldades estende o alcance e a importância dos métodos utilizados na avaliação de resultados. No entanto, não podemos esquecer que a consolidação das estruturas pode nos levar a considerar a reestruturação das regras de conduta normativas.
                        
                Ainda assim, existem dúvidas a respeito de como o fenômeno da Internet possibilita uma melhor visão global dos conhecimentos estratégicos para atingir a excelência. O cuidado em identificar pontos críticos na hegemonia do ambiente político ainda não demonstrou convincentemente que vai participar na mudança das condições inegavelmente apropriadas. Pensando mais a longo prazo, o comprometimento entre as equipes estimula a padronização de todos os recursos funcionais envolvidos. Todas estas questões, devidamente ponderadas, levantam dúvidas sobre se a constante divulgação das informações aponta para a melhoria do levantamento das variáveis envolvidas. Gostaria de enfatizar que a crescente influência da mídia causa impacto indireto na reavaliação dos modos de operação convencionais.
                        
                O incentivo ao avanço tecnológico, assim como a necessidade de renovação processual agrega valor ao estabelecimento das formas de ação. Podemos já vislumbrar o modo pelo qual o novo modelo estrutural aqui preconizado é uma das consequências de alternativas às soluções ortodoxas. Nunca é demais lembrar o peso e o significado destes problemas, uma vez que a execução dos pontos do programa promove a alavancagem dos procedimentos normalmente adotados. Desta maneira, a expansão dos mercados mundiais nos obriga à análise do fluxo de informações.
                        
                É importante questionar o quanto o início da atividade geral de formação de atitudes acarreta um processo de reformulação e modernização dos níveis de motivação departamental. Neste sentido, a complexidade dos estudos efetuados oferece uma interessante oportunidade para verificação das condições financeiras e administrativas exigidas. O empenho em analisar a determinação clara de objetivos exige a precisão e a definição das diversas correntes de pensamento. O que temos que ter sempre em mente é que a adoção de políticas descentralizadoras faz parte de um processo de gerenciamento do impacto na agilidade decisória. É claro que a valorização de fatores subjetivos desafia a capacidade de equalização das novas proposições.
                        
                Percebemos, cada vez mais, que a revolução dos costumes apresenta tendências no sentido de aprovar a manutenção do processo de comunicação como um todo. Acima de tudo, é fundamental ressaltar que o entendimento das metas propostas afeta positivamente a correta previsão do sistema de formação de quadros que corresponde às necessidades. Todavia, o consenso sobre a necessidade de qualificação assume importantes posições no estabelecimento do orçamento setorial. A certificação de metodologias que nos auxiliam a lidar com o desafiador cenário globalizado representa uma abertura para a melhoria do remanejamento dos quadros funcionais.
                        
                A prática cotidiana prova que o surgimento do comércio virtual deve passar por modificações independentemente das condições inegavelmente apropriadas. Neste sentido, a percepção das dificuldades cumpre um papel essencial na formulação das diretrizes de desenvolvimento para o futuro. No mundo atual, o acompanhamento das preferências de consumo garante a contribuição de um grupo importante na determinação das direções preferenciais no sentido do progresso. Por conseguinte, o julgamento imparcial das eventualidades auxilia a preparação e a composição da gestão inovadora da qual fazemos parte. Ainda assim, existem dúvidas a respeito de como a necessidade de renovação processual promove a alavancagem das novas proposições.
                        
                Pensando mais a longo prazo, a determinação clara de objetivos ainda não demonstrou convincentemente que vai participar na mudança dos níveis de motivação departamental. Não obstante, o comprometimento entre as equipes é uma das consequências das formas de ação. Assim mesmo, o desafiador cenário globalizado maximiza as possibilidades por conta dos relacionamentos verticais entre as hierarquias.
                """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' must be between 1 and 4000 characters";

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedMembers
        );

        // when
        final var actualError = Assertions.assertThrows(
                DomainException.class,
                () -> actualVideo.validate(new ThrowsValidationHandler())
        );

        // then
        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }

    @Test
    public void givenNullLaunchedAt_whenCallsValidate_shouldReceiveError() {
        // given
        final var expectedTitle = "System Design Interviews";
        final var expectedDescription = "A description";
        final Year expectedLaunchedAt = null;
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'launchedAt' should not be null";

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedMembers
        );

        // when
        final var actualError = Assertions.assertThrows(
                DomainException.class,
                () -> actualVideo.validate(new ThrowsValidationHandler())
        );

        // then
        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }

    @Test
    public void givenNullRating_whenCallsValidate_shouldReceiveError() {
        // given
        final var expectedTitle = "System Design Interviews";
        final var expectedDescription = "A description";
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 120.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final Rating expectedRating = null;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenres = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'rating' should not be null";

        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating,
                expectedCategories,
                expectedGenres,
                expectedMembers
        );

        // when
        final var actualError = Assertions.assertThrows(
                DomainException.class,
                () -> actualVideo.validate(new ThrowsValidationHandler())
        );

        // then
        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }
}
