package io.github.wojtekmarcin.memobox.controller;

import io.github.wojtekmarcin.memobox.controller.REST.UserRESTController;
import io.github.wojtekmarcin.memobox.entities.User;
import io.github.wojtekmarcin.memobox.entities.WordsSet;
import io.github.wojtekmarcin.memobox.repository.UserRepository;
import io.github.wojtekmarcin.memobox.repository.WordsSetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/wordset")
public class WordSetController {
    public static final String PAGE_WORDSET_ADD = "wordset/add";
    public static final String PAGE_WORDSET_EDIT = "wordset/edit";
    public static final String REDIRECT_PAGE_WORDSET_VIEW = "redirect:/wordset/view";

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRESTController.class);

    private final WordsSetRepository wordSetRepository;
    private final UserRepository userRepository;

    public WordSetController(WordsSetRepository wordsSetRepository, UserRepository userRepository) {
        this.wordSetRepository = wordsSetRepository;
        this.userRepository = userRepository;
    }

    @ModelAttribute("authUser")
    private User getAuthUser(Principal principal) {
        return userRepository.findUserByUsername(principal.getName());
    }

    @GetMapping("/view")
    private String showWordView(Model model) {
        model.addAttribute("wordsets", wordSetRepository.findAll());
        return "wordset/view";
    }

    @GetMapping("/addWordSet")
    private String initAddWordForm(Model model, @ModelAttribute("authUser") User user) {
        WordsSet wordsSet = new WordsSet();
        user.getWordsSetId().add(wordsSet);
        model.addAttribute("wordsetToAdd", wordsSet);
        return PAGE_WORDSET_ADD;
    }

    @PostMapping("/addWordSet")
    private String processAddingWordEntityForm(@ModelAttribute("wordsetToAdd")
                                               @Valid WordsSet wordsSet,
                                               BindingResult bindingResult,
                                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return PAGE_WORDSET_ADD;
        } else {
            wordSetRepository.save(wordsSet);
            redirectAttributes.addFlashAttribute("message", String.format("Wordset %s created.", wordsSet.getWordSetName()));
            return REDIRECT_PAGE_WORDSET_VIEW;
        }
    }

    @GetMapping("/deleteWordSet/{id}")
    private String initDeleteUserEntityForm(@PathVariable("id") long id) {
        wordSetRepository.deleteByWordSetId(id);
        return REDIRECT_PAGE_WORDSET_VIEW;
    }

    @GetMapping("/editWordSet/{id}")
    private String initEditWordEntitieForm(@PathVariable("id") long id, Model model) {
        model.addAttribute("wordSetFromSource", wordSetRepository.findMemoBoxByWordSetId(id));
        return PAGE_WORDSET_EDIT;
    }

    @PostMapping("/editWordSet/{id}")
    private String processEditWordEntitieForm(@PathVariable("id") long id,
                                      @ModelAttribute("wordSetFromSource")
                                      @Valid WordsSet wordsSet,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return PAGE_WORDSET_EDIT;
        } else {
            LOGGER.info("word input ={}", wordsSet);

            wordsSet.setWordSetId(id);
            wordSetRepository.save(wordsSet);

            LOGGER.info("users output={}", wordsSet);
            return REDIRECT_PAGE_WORDSET_VIEW;
        }
    }
}

