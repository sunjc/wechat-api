package org.itrunner.wechat.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.itrunner.wechat.domain.Hero;
import org.itrunner.wechat.service.HeroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/heroes", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {"Hero Controller"})
@Slf4j
public class HeroController {
    private final HeroService service;

    @Autowired
    public HeroController(HeroService service) {
        this.service = service;
    }

    @ApiOperation("Get hero by id")
    @GetMapping("/{id}")
    public Hero getHeroById(@ApiParam(required = true, example = "1") @PathVariable("id") Long id) {
        return service.getHeroById(id);
    }

    @ApiOperation("Get all heroes")
    @GetMapping
    public List<Hero> getHeroes() {
        return service.getAllHeroes();
    }

    @ApiOperation("Search heroes by name")
    @GetMapping("/")
    public List<Hero> searchHeroes(@ApiParam(required = true) @RequestParam("name") String name) {
        return service.findHeroesByName(name);
    }

    @ApiOperation("Add new hero")
    @PostMapping
    public Hero addHero(@ApiParam(required = true) @Valid @RequestBody Hero hero) {
        return service.saveHero(hero);
    }

    @ApiOperation("Update hero info")
    @PutMapping
    public Hero updateHero(@ApiParam(required = true) @Valid @RequestBody Hero hero) {
        return service.saveHero(hero);
    }

    @ApiOperation("Delete hero by id")
    @DeleteMapping("/{id}")
    public void deleteHero(@ApiParam(required = true, example = "1") @PathVariable("id") Long id) {
        service.deleteHero(id);
    }
}