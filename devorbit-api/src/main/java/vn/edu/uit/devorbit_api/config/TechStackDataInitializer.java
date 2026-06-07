package vn.edu.uit.devorbit_api.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
import vn.edu.uit.devorbit_api.entity.TechStack;
import vn.edu.uit.devorbit_api.repository.GithubRepoRepository;
import vn.edu.uit.devorbit_api.repository.TechStackRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * One-time initializer that migrates old single-value tech_stack and
 * primary_language fields into the new ManyToMany tech_stacks + repo_tech_stacks.
 *
 * Runs only for repos that have ZERO tech stacks linked via the join table.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TechStackDataInitializer {

    private final GithubRepoRepository githubRepoRepository;
    private final TechStackRepository techStackRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        List<GithubRepo> repos = githubRepoRepository.findAllWithTechStacks();
        java.util.Map<String, TechStack> techStackCache = new java.util.HashMap<>();
        for (TechStack ts : techStackRepository.findAll()) {
            if (ts.getName() != null) {
                techStackCache.put(ts.getName().trim().toLowerCase(Locale.ROOT), ts);
            }
        }
        int updated = 0;
        List<GithubRepo> reposToUpdate = new java.util.ArrayList<>();

        for (GithubRepo repo : repos) {
            if (repo.getTechStacks() != null && !repo.getTechStacks().isEmpty()) {
                continue; // already has tech stacks via ManyToMany
            }

            Set<TechStack> stacks = new LinkedHashSet<>();
            Set<String> seen = new LinkedHashSet<>();

            // 1. Migrate old single-value tech_stack column
            if (repo.getTechStack() != null && !repo.getTechStack().isBlank()) {
                String name = repo.getTechStack().trim();
                String key = name.toLowerCase(Locale.ROOT);
                if (seen.add(key)) {
                    stacks.add(resolveStack(name, techStackCache));
                }
            }

            // 2. Derive from primary_language (e.g., "Java", "Python", "TypeScript")
            if (repo.getPrimaryLanguage() != null && !repo.getPrimaryLanguage().isBlank()) {
                String name = repo.getPrimaryLanguage().trim();
                String key = name.toLowerCase(Locale.ROOT);
                if (seen.add(key)) {
                    stacks.add(resolveStack(name, techStackCache));
                }
            }

            if (!stacks.isEmpty()) {
                repo.setTechStacks(stacks);
                reposToUpdate.add(repo);
                updated++;
            }
        }

        if (!reposToUpdate.isEmpty()) {
            githubRepoRepository.saveAll(reposToUpdate);
            log.info("TechStackDataInitializer: populated tech stacks for {} repos", updated);
        }
    }

    private TechStack resolveStack(String name, java.util.Map<String, TechStack> cache) {
        String key = name.trim().toLowerCase(Locale.ROOT);
        return cache.computeIfAbsent(key, k -> techStackRepository.save(
                TechStack.builder().name(name.trim()).build()
        ));
    }
}
