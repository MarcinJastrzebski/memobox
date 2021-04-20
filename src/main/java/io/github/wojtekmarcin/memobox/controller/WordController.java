package io.github.wojtekmarcin.memobox.controller;

import io.github.wojtekmarcin.memobox.controller.REST.UserRESTController;
import io.github.wojtekmarcin.memobox.dictionary.LanguageEnum;
import io.github.wojtekmarcin.memobox.entities.Word;
import io.github.wojtekmarcin.memobox.entities.WordsSet;
import io.github.wojtekmarcin.memobox.repository.WordRepository;
import io.github.wojtekmarcin.memobox.repository.WordsSetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/word")
public class WordController {
    public static final String PAGE_WORD_ADD = "word/add";
    public static final String PAGE_WORD_EDIT = "word/edit";
    public static final String REDIRECT_PAGE_WORD_VIEW = "redirect:/word/view";

    private static final Logger LOGGER = LoggerFactory.getLogger(WordController.class);

    private final WordRepository wordRepository;
    private final WordsSetRepository wordsSetRepository;

    public WordController(WordRepository repository, WordsSetRepository wordsSetRepository) {
        this.wordRepository = repository;
        this.wordsSetRepository = wordsSetRepository;
    }

    @ModelAttribute("wordSets")
    public List<WordsSet> getWordSets() {
        return wordsSetRepository.findAll();
            }

    @ModelAttribute("languages")
    public LanguageEnum[] getLanguage() {
        return LanguageEnum.values();
    }

    @GetMapping("/view")
    String showWordView(Model model) {
        model.addAttribute("words", wordRepository.findAll());
        return "word/view";
    }

    @GetMapping("/addWord")
    private String initAddWordForm(Model model) {
        model.addAttribute("wordToAdd", new Word());
        return PAGE_WORD_ADD;
    }

    @PostMapping("/addWord")
    private String processAddingWordEntityForm(@ModelAttribute("wordToAdd") @Valid Word word,
                                               BindingResult bindingResult,
                                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            LOGGER.info("Add word method has errors ={}", bindingResult.getAllErrors());
            return PAGE_WORD_ADD;
        } else {
            wordRepository.save(word);
            redirectAttributes.addFlashAttribute("message", String.format("Word %s added", word.getWord().toLowerCase()));
            return REDIRECT_PAGE_WORD_VIEW;
        }
    }

    @GetMapping("/deleteWord/{id}")
    String initDeleteUserEntityForm(@PathVariable("id") long id,
                                    RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", String.format("Word %s deleted", wordRepository.findWordByWordId(id).getWord().toLowerCase()));
        wordRepository.deleteWordByWordId(id);
        return REDIRECT_PAGE_WORD_VIEW;
    }

    @GetMapping("/editWord/{id}")
    String initEditWordEntitieForm(@PathVariable("id") long id, Model model) {
        model.addAttribute("wordFromSource", wordRepository.findWordByWordId(id));
        return PAGE_WORD_EDIT;
    }

    @PostMapping("/editWord/{id}")
    String processEditWordEntitieForm(@PathVariable("id") long id,
                                      @ModelAttribute("wordFromSource")
                                      @Valid Word word,
                                      BindingResult bindingResult,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            LOGGER.info("Edit word method has errors ={}", bindingResult.getAllErrors());
            word.setWordId(id);
            model.addAttribute("WordFromSource", word);
            return PAGE_WORD_EDIT;
        } else {
            LOGGER.info("Input ={}", word);
            word.setWordId(id);
            wordRepository.save(word);
            LOGGER.info("Output={}", word);

            redirectAttributes.addFlashAttribute("message", String.format("Word %s edited", word.getWord().toLowerCase()));
            return REDIRECT_PAGE_WORD_VIEW;
        }
    }
}
