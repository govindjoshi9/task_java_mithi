import java.io.*;
import java.util.*;

class Word {
    private String word;
    private Set<Integer> pageNumbers;

    public Word(String word, int pageNumber) {
        this.word = word;
        this.pageNumbers = new HashSet<>();
        this.pageNumbers.add(pageNumber);
    }

    public String getWord() {
        return this.word;
    }

    public Set<Integer> getPageNumbers() {
        return this.pageNumbers;
    }

    public void addPageNumber(int pageNumber) {
        this.pageNumbers.add(pageNumber);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Word)) {
            return false;
        }
        Word other = (Word) obj;
        return this.word.equals(other.word);
    }

    @Override
    public int hashCode() {
        return this.word.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.word).append(" : ");
        List<Integer> sortedPageNumbers = new ArrayList<>(this.pageNumbers);
        Collections.sort(sortedPageNumbers);
        for (int i = 0; i < sortedPageNumbers.size(); i++) {
            sb.append(sortedPageNumbers.get(i));
            if (i != sortedPageNumbers.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}

class Page {
    private int pageNumber;
    private String content;

    public Page(int pageNumber, String content) {
        this.pageNumber = pageNumber;
        this.content = content;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public String getContent() {
        return this.content;
    }
}

class StopWords {
    private Set<String> stopWords;

    public StopWords(String fileName) throws IOException {
        this.stopWords = new HashSet<>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = null;
        while ((line = reader.readLine()) != null) {
            this.stopWords.add(line.trim());
        }
        reader.close();
    }

    public boolean contains(String word) {
        return this.stopWords.contains(word);
    }
}

public class WordIndexer {
    private List<Page> pages;
    private Set<String> stopWords;
    private List<Word> index;

    public WordIndexer() {
        this.pages = new ArrayList<>();
        this.stopWords = new HashSet<>();
        this.index = new ArrayList<>();
    }

    public void addPage(Page page) {
        this.pages.add(page);
    }

    public void addStopWord(String word) {
        this.stopWords.add(word);
    }

    public void buildIndex() {
        for (Page page : this.pages) {
            String content = page.getContent().toLowerCase();
            String[] words = content.split("[^a-zA-Z0-9']+");
            for (String word : words) {
                if (!this.stopWords.contains(word) && word.length() > 0) {
                    Word indexedWord = new Word(word, page.getPageNumber());
                    int index = this.index.indexOf(indexedWord);
                    if (index >= 0) {
                        this.index.get(index).addPageNumber(page.getPageNumber());
                    } else {
                        this.index.add(indexedWord);
                    }
                }
            }
        }
    }

    public void writeIndexToFile(String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        Collections.sort(this.index, Comparator.comparing(Word::getWord));
        for (Word word : this.index) {
            writer.write(word.toString());
            writer.newLine();
        }
        writer.close();
    }



    public static void main(String[] args) throws IOException {
        WordIndexer indexer = new WordIndexer();
        indexer.addStopWord("and"); // add additional stop words
        indexer.addStopWord("the");
        indexer.addStopWord("a");
        // read page files and add them to indexer
        String[] fileNames = {"Page1.txt", "Page2.txt", "Page3.txt"};
        for (String fileName : fileNames) {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line = null;
            int pageNumber = Integer.parseInt(fileName.substring(4, 5));
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            Page page = new Page(pageNumber, sb.toString());
            indexer.addPage(page);
            reader.close();
        }
        indexer.buildIndex();
        indexer.writeIndexToFile("index.txt");
    }
}


