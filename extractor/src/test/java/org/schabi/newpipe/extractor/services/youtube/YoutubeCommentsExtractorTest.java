package org.schabi.newpipe.extractor.services.youtube;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.schabi.newpipe.downloader.DownloaderFactory;
import org.schabi.newpipe.extractor.ListExtractor.InfoItemsPage;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.comments.CommentsInfo;
import org.schabi.newpipe.extractor.comments.CommentsInfoItem;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.services.DefaultTests;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeCommentsExtractor;
import org.schabi.newpipe.extractor.utils.Utils;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.schabi.newpipe.extractor.ServiceList.YouTube;

public class YoutubeCommentsExtractorTest {
    private static final String RESOURCE_PATH = DownloaderFactory.RESOURCE_PATH + "services/youtube/extractor/comments/";

    /**
     * Test a "normal" YouTube
     */
    public static class Thomas {
        private static final String url = "https://www.youtube.com/watch?v=D00Au7k3i6o";
        private static final String commentContent = "Category: Education";
        private static YoutubeCommentsExtractor extractor;

        @BeforeClass
        public static void setUp() throws Exception {
            YoutubeParsingHelper.resetClientVersionAndKey();
            NewPipe.init(new DownloaderFactory().getDownloader(RESOURCE_PATH + "thomas"));
            extractor = (YoutubeCommentsExtractor) YouTube
                    .getCommentsExtractor(url);
            extractor.fetchPage();
        }

        @Test
        public void testGetComments() throws IOException, ExtractionException {
            assertTrue(getCommentsHelper(extractor));
        }

        private boolean getCommentsHelper(YoutubeCommentsExtractor extractor) throws IOException, ExtractionException {
            InfoItemsPage<CommentsInfoItem> comments = extractor.getInitialPage();
            boolean result = findInComments(comments, commentContent);

            while (comments.hasNextPage() && !result) {
                comments = extractor.getPage(comments.getNextPage());
                result = findInComments(comments, commentContent);
            }

            return result;
        }

        @Test
        public void testGetCommentsFromCommentsInfo() throws IOException, ExtractionException {
            assertTrue(getCommentsFromCommentsInfoHelper(url));
        }

        private boolean getCommentsFromCommentsInfoHelper(String url) throws IOException, ExtractionException {
            final CommentsInfo commentsInfo = CommentsInfo.getInfo(url);

            assertEquals("Comments", commentsInfo.getName());
            boolean result = findInComments(commentsInfo.getRelatedItems(), commentContent);

            Page nextPage = commentsInfo.getNextPage();
            InfoItemsPage<CommentsInfoItem> moreItems = new InfoItemsPage<>(null, nextPage, null);
            while (moreItems.hasNextPage() && !result) {
                moreItems = CommentsInfo.getMoreItems(YouTube, commentsInfo, nextPage);
                result = findInComments(moreItems.getItems(), commentContent);
                nextPage = moreItems.getNextPage();
            }
            return result;
        }

        @Test
        public void testGetCommentsAllData() throws IOException, ExtractionException {
            InfoItemsPage<CommentsInfoItem> comments = extractor.getInitialPage();

            DefaultTests.defaultTestListOfItems(YouTube, comments.getItems(), comments.getErrors());
            for (CommentsInfoItem c : comments.getItems()) {
                assertFalse(Utils.isBlank(c.getUploaderUrl()));
                assertFalse(Utils.isBlank(c.getUploaderName()));
                assertFalse(Utils.isBlank(c.getUploaderAvatarUrl()));
                assertFalse(Utils.isBlank(c.getCommentId()));
                assertFalse(Utils.isBlank(c.getCommentText()));
                assertFalse(Utils.isBlank(c.getName()));
                assertFalse(Utils.isBlank(c.getTextualUploadDate()));
                assertNotNull(c.getUploadDate());
                assertFalse(Utils.isBlank(c.getThumbnailUrl()));
                assertFalse(Utils.isBlank(c.getUrl()));
                assertFalse(c.getLikeCount() < 0);
            }
        }

        private boolean findInComments(InfoItemsPage<CommentsInfoItem> comments, String comment) {
            return findInComments(comments.getItems(), comment);
        }

        private boolean findInComments(List<CommentsInfoItem> comments, String comment) {
            for (CommentsInfoItem c : comments) {
                if (c.getCommentText().contains(comment)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Test a video with an empty comment
     */
    public static class EmptyComment {
        private final static String url = "https://www.youtube.com/watch?v=VM_6n762j6M";
        private static YoutubeCommentsExtractor extractor;

        @BeforeClass
        public static void setUp() throws Exception {
            YoutubeParsingHelper.resetClientVersionAndKey();
            NewPipe.init(new DownloaderFactory().getDownloader(RESOURCE_PATH + "empty"));
            extractor = (YoutubeCommentsExtractor) YouTube
                    .getCommentsExtractor(url);
            extractor.fetchPage();
        }

        @Test
        @Ignore("TODO fix")
        public void testGetCommentsAllData() throws IOException, ExtractionException {
            final InfoItemsPage<CommentsInfoItem> comments = extractor.getInitialPage();

            DefaultTests.defaultTestListOfItems(YouTube, comments.getItems(), comments.getErrors());
            for (CommentsInfoItem c : comments.getItems()) {
                assertFalse(Utils.isBlank(c.getUploaderUrl()));
                assertFalse(Utils.isBlank(c.getUploaderName()));
                assertFalse(Utils.isBlank(c.getUploaderAvatarUrl()));
                assertFalse(Utils.isBlank(c.getCommentId()));
                assertFalse(Utils.isBlank(c.getName()));
                assertFalse(Utils.isBlank(c.getTextualUploadDate()));
                assertNotNull(c.getUploadDate());
                assertFalse(Utils.isBlank(c.getThumbnailUrl()));
                assertFalse(Utils.isBlank(c.getUrl()));
                assertFalse(c.getLikeCount() < 0);
                if (c.getCommentId().equals("Ugga_h1-EXdHB3gCoAEC")) { // comment without text
                    assertTrue(Utils.isBlank(c.getCommentText()));
                } else {
                    assertFalse(Utils.isBlank(c.getCommentText()));
                }
            }
        }

    }

    public static class HeartedByCreator {
        private final static String url = "https://www.youtube.com/watch?v=tR11b7uh17Y";
        private static YoutubeCommentsExtractor extractor;

        @BeforeClass
        public static void setUp() throws Exception {
            YoutubeParsingHelper.resetClientVersionAndKey();
            NewPipe.init(new DownloaderFactory().getDownloader(RESOURCE_PATH + "hearted"));
            extractor = (YoutubeCommentsExtractor) YouTube
                    .getCommentsExtractor(url);
            extractor.fetchPage();
        }

        @Test
        public void testGetCommentsAllData() throws IOException, ExtractionException {
            final InfoItemsPage<CommentsInfoItem> comments = extractor.getInitialPage();

            DefaultTests.defaultTestListOfItems(YouTube, comments.getItems(), comments.getErrors());

            boolean heartedByUploader = false;

            for (CommentsInfoItem c : comments.getItems()) {
                assertFalse(Utils.isBlank(c.getUploaderUrl()));
                assertFalse(Utils.isBlank(c.getUploaderName()));
                assertFalse(Utils.isBlank(c.getUploaderAvatarUrl()));
                assertFalse(Utils.isBlank(c.getCommentId()));
                assertFalse(Utils.isBlank(c.getName()));
                assertFalse(Utils.isBlank(c.getTextualUploadDate()));
                assertNotNull(c.getUploadDate());
                assertFalse(Utils.isBlank(c.getThumbnailUrl()));
                assertFalse(Utils.isBlank(c.getUrl()));
                assertFalse(c.getLikeCount() < 0);
                assertFalse(Utils.isBlank(c.getCommentText()));
                if (c.isHeartedByUploader()) {
                    heartedByUploader = true;
                }
            }
            assertTrue("No comments was hearted by uploader", heartedByUploader);

        }
    }

    public static class Pinned {
        private final static String url = "https://www.youtube.com/watch?v=bjFtFMilb34";
        private static YoutubeCommentsExtractor extractor;

        @BeforeClass
        public static void setUp() throws Exception {
            YoutubeParsingHelper.resetClientVersionAndKey();
            NewPipe.init(new DownloaderFactory().getDownloader(RESOURCE_PATH + "pinned"));
            extractor = (YoutubeCommentsExtractor) YouTube
                    .getCommentsExtractor(url);
            extractor.fetchPage();
        }

        @Test
        public void testGetCommentsAllData() throws IOException, ExtractionException {
            final InfoItemsPage<CommentsInfoItem> comments = extractor.getInitialPage();

            DefaultTests.defaultTestListOfItems(YouTube, comments.getItems(), comments.getErrors());

            for (CommentsInfoItem c : comments.getItems()) {
                assertFalse(Utils.isBlank(c.getUploaderUrl()));
                assertFalse(Utils.isBlank(c.getUploaderName()));
                assertFalse(Utils.isBlank(c.getUploaderAvatarUrl()));
                assertFalse(Utils.isBlank(c.getCommentId()));
                assertFalse(Utils.isBlank(c.getName()));
                assertFalse(Utils.isBlank(c.getTextualUploadDate()));
                assertNotNull(c.getUploadDate());
                assertFalse(Utils.isBlank(c.getThumbnailUrl()));
                assertFalse(Utils.isBlank(c.getUrl()));
                assertFalse(c.getLikeCount() < 0);
                assertFalse(Utils.isBlank(c.getCommentText()));
            }

            assertTrue("First comment isn't pinned", comments.getItems().get(0).isPinned());
        }
    }
}
