package org.itrunner.wechat.service;

import org.itrunner.wechat.domain.Hero;
import org.itrunner.wechat.exception.HeroNotFoundException;
import org.itrunner.wechat.repository.HeroRepository;
import org.itrunner.wechat.util.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HeroService {
    private final HeroRepository repository;
    private final Messages messages;

    @Autowired
    public HeroService(HeroRepository repository, Messages messages) {
        this.repository = repository;
        this.messages = messages;
    }

    public Hero getHeroById(Long id) {
        return repository.findById(id).orElseThrow(() -> new HeroNotFoundException(messages.getMessage("hero.notFound", new Object[]{id})));
    }

    public List<Hero> getAllHeroes() {
        return repository.findAll();
    }

    public List<Hero> findHeroesByName(String name) {
        return repository.findByName(name);
    }

    public Hero saveHero(Hero hero) {
        return repository.save(hero);
    }

    public void deleteHero(Long id) {
        repository.deleteById(id);
    }
}
