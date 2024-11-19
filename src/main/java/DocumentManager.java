import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    private final Map<String, Document> documentStorage = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {

        // Generation of unique ID if id does not exist
        if (document.getId() == null || document.getId().isEmpty()) {
            document.setId(UUID.randomUUID().toString());
        }
        // Setting the time of creation
        document.setCreated(documentStorage.containsKey(document.getId()) ? document.getCreated() : Instant.now());
        // Adding document to the storage
        documentStorage.put(document.getId(), document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        List<Document> searchedDocuments = new ArrayList<>();

        // Check all the documents by matchesSearchRequest method
        for (Document document : documentStorage.values()) {
            if (matchesSearchRequest(document, request)) {
                searchedDocuments.add(document);
            }
        }

        return searchedDocuments;
    }

    /**
     * Checks if a document matches the given search request.
     *
     * @param document the document to check
     * @param request  the search request with filters
     * @return true if the document matches, false if it does not
     */
    private boolean matchesSearchRequest(Document document, SearchRequest request) {
        // In case no filters specified, match all documents
        if (request == null) {
            return true;
        }

        // Checking title prefixes
        if (request.getTitlePrefixes() != null) {
            boolean matchesTitle = false;
            for (String prefix : request.getTitlePrefixes()) {
                if (document.getTitle().startsWith(prefix)) {
                    matchesTitle = true;
                    break;
                }
            }
            if (!matchesTitle) {
                return false;
            }
        }

        // Checking content substrings
        if (request.getContainsContents() != null) {
            boolean matchesContent = false;
            for (String content : request.getContainsContents()) {
                if (document.getContent().contains(content)) {
                    matchesContent = true;
                    break;
                }
            }
            if (!matchesContent) {
                return false;
            }
        }

        // Checking author IDs
        if (request.getAuthorIds() != null) {
            if (!request.getAuthorIds().contains(document.getAuthor().getId())) {
                return false;
            }
        }

        // Checking creation time range
        if (request.getCreatedFrom() != null && document.getCreated().isBefore(request.getCreatedFrom())) {
            return false;
        }
        if (request.getCreatedTo() != null && document.getCreated().isAfter(request.getCreatedTo())) {
            return false;
        }

        return true;
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        //In case it has value it will return it, otherwise an empty Optional
        return Optional.ofNullable(documentStorage.get(id));

    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }

}