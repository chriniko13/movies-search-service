package com.chriniko.lunatech.movies.service.mapper;

import com.chriniko.lunatech.movies.domain.*;
import com.chriniko.lunatech.movies.dto.core.*;
import com.chriniko.lunatech.movies.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class TitleMapperService {

    @Autowired
    private RatingsRepository ratingsRepository;

    @Autowired
    private AkaRepository akaRepository;

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private NamesRepository namesRepository;

    @Autowired
    private PrincipalsRepository principalsRepository;

    @Autowired
    private EpisodesRepository episodesRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Title map(Basic basic, boolean fullFetch) {
        return _map(basic, fullFetch);
    }

    public Title mapPerThread(Basic basic, boolean fullFetch) {
        return transactionTemplate.execute(new TransactionCallback<Title>() {
            @Override
            public Title doInTransaction(TransactionStatus txStatus) {
                return _map(basic, fullFetch);
            }
        });
    }

    private Title _map(Basic basic, boolean fullFetch) {
        final String tconst = basic.getTconst();

        Title.TitleBuilder titleBuilder = addBasicInfo(basic, tconst);
        addRatingInfo(tconst, titleBuilder);

        if (fullFetch) {
            addCrewInfo(tconst, titleBuilder);
            addAlternativeTitlesInfo(tconst, titleBuilder);
            addPrincipalInfo(tconst, titleBuilder);
            addEpisodeInfo(tconst, titleBuilder);
        }
        return titleBuilder.build();
    }

    private Title.TitleBuilder addBasicInfo(Basic result, String tconst) {
        return Title.builder()
                .id(tconst)
                .titleType(result.getTitleType())
                .primaryTitle(result.getPrimaryTitle())
                .originalTitle(result.getOriginalTitle())
                .isAdult(result.isAdult())
                .startYear(result.getStartYear())
                .endYear(result.getEndYear())
                .runtimeMinutes(result.getRuntimeMinutes())
                .genres(result.getGenres());
    }

    private void addRatingInfo(String tconst, Title.TitleBuilder titleBuilder) {
        final Optional<Rating> rating = ratingsRepository.find(tconst);
        rating.ifPresent(r -> {
            titleBuilder.averageRating(r.getAverageRating());
            titleBuilder.numVotes(r.getNumVotes());
        });
    }

    private void addCrewInfo(String tconst, Title.TitleBuilder titleBuilder) {
        final Optional<Crew> crew = crewRepository.find(tconst);
        crew.ifPresent(c -> {

            c.getDirectorsIds()
                    .forEach(directorId -> {
                        namesRepository.find(directorId)
                                .ifPresent(n -> {
                                    Person person = map(n);
                                    titleBuilder.director(person);
                                });
                    });

            c.getWritersIds()
                    .forEach(writerId -> {
                        namesRepository.find(writerId)
                                .ifPresent(w -> {
                                    Person person = map(w);
                                    titleBuilder.writer(person);
                                });
                    });

        });
    }

    private void addAlternativeTitlesInfo(String tconst, Title.TitleBuilder titleBuilder) {
        final List<Aka> akas = akaRepository.find(tconst);
        akas.forEach(aka -> {
            AlternativeTitle alternativeTitle = map(aka);
            titleBuilder.alternativeTitle(alternativeTitle);
        });
    }

    private void addPrincipalInfo(String tconst, Title.TitleBuilder titleBuilder) {
        List<Principal> principals = principalsRepository.find(tconst);
        principals.forEach(p -> {

            PrincipalPerson.PrincipalPersonBuilder principalPersonBuilder = PrincipalPerson.builder()
                    .titleId(p.getId().getTconst())
                    .ordering(p.getId().getOrdering())
                    .category(p.getCategory())
                    .job(p.getJob())
                    .characters(p.getCharacters());

            Optional<Name> name = namesRepository.find(p.getNconst());
            name.ifPresent(n -> {
                Person person = map(n);
                principalPersonBuilder.personInfo(person);
            });

            titleBuilder.principal(principalPersonBuilder.build());
        });
    }

    private void addEpisodeInfo(String tconst, Title.TitleBuilder titleBuilder) {
        List<Episode> episodes = episodesRepository.find(tconst);
        episodes.forEach(e -> {
            EpisodeInfo episodeInfo = map(e);
            titleBuilder.episodeInfo(episodeInfo);
        });
    }

    public Person map(Name n) {
        return Person.builder()
                .id(n.getNconst())
                .primaryName(n.getPrimaryName())
                .birthYear(n.getBirthYear())
                .deathYear(n.getDeathYear())
                .primaryProfessions(n.getPrimaryProfessionsSplited())
                .knownForTitles(n.getKnownForTitlesSplited())
                .build();
    }

    private EpisodeInfo map(Episode e) {
        return EpisodeInfo.builder()
                .id(e.getTconst())
                .titleId(e.getParentTconst())
                .seasonNumber(e.getSeasonNumber())
                .episodeNumber(e.getEpisodeNumber())
                .build();
    }

    private AlternativeTitle map(Aka aka) {
        return AlternativeTitle.builder()
                .titleId(aka.getId().getTitleId())
                .ordering(aka.getId().getOrdering())
                .title(aka.getTitle())
                .region(aka.getRegion())
                .language(aka.getLanguage())
                .types(aka.getTypesSplited())
                .attributes(aka.getAttributesSplited())
                .isOriginalTitle(aka.isOriginalTitle())
                .build();
    }
}
