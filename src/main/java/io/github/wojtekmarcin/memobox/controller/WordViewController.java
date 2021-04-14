package io.github.wojtekmarcin.memobox.controller;

import io.github.wojtekmarcin.memobox.entities.Audit;
import io.github.wojtekmarcin.memobox.entities.Word;
import io.github.wojtekmarcin.memobox.entities.WordsSet;
import io.github.wojtekmarcin.memobox.repository.WordRepository;
import io.github.wojtekmarcin.memobox.repository.WordsSetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/word")
public class WordViewController {
    public static final String PAGE_WORD_ADD = "word/add";
    public static final String PAGE_WORD_EDIT = "word/edit";
    public static final String REDIRECT_PAGE_WORD_VIEW = "redirect:/word/view";

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final WordRepository wordRepository;
    private final WordsSetRepository wordsSetRepository;

    public WordViewController(WordRepository repository, WordsSetRepository wordsSetRepository) {
        this.wordRepository = repository;
        this.wordsSetRepository = wordsSetRepository;
    }

    @ModelAttribute("wordSets")
    public List<WordsSet> getWordSets() {
        return wordsSetRepository.findAll();
    }

    @GetMapping("/view")
    String showWordView(Model model) {
        model.addAttribute("words", wordRepository.findAll());
        return "word/view";
    }

    /*TODO
        -Nie dodaje się WordSet
      */
    @GetMapping("/addWord")
    private String initAddWordForm(Model model) {
        model.addAttribute("wordToAdd", new Word());
        return PAGE_WORD_ADD;
    }

    @PostMapping("/addWord")
    private String processAddingWordEntityForm(@ModelAttribute("wordToAdd") @Valid Word word,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return PAGE_WORD_ADD;
        } else {
            wordRepository.save(word);
            return REDIRECT_PAGE_WORD_VIEW;
        }
    }

    @GetMapping("/deleteWord/{id}")
    String initDeleteUserEntityForm(@PathVariable("id") long id) {
        wordRepository.deleteWordByWordId(id);
        return REDIRECT_PAGE_WORD_VIEW;
    }

    @GetMapping("/editWord/{id}")
    String initEditWordEntitieForm(@PathVariable("id") long id, Model model) {
        model.addAttribute("wordFromSource", wordRepository.findWordByWordId(id));
        return PAGE_WORD_EDIT;
    }

    /*TODO
    *   - przy edytowaniu encji nadpisuje się na null wartość audit.createdOn()
    * */

    @PostMapping("/editWord/{id}")
    String processEditWordEntitieForm(@PathVariable("id") long id,
                                      @ModelAttribute("wordFromSource")
                                      @Valid Word word,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return PAGE_WORD_EDIT;
        } else {
            LOGGER.info("word input ={}", word);

            word.setWordId(id);
            wordRepository.save(word);

            LOGGER.info("users output={}", word);
            return REDIRECT_PAGE_WORD_VIEW;
        }
    }
}
