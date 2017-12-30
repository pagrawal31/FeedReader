package com.example.test;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.File;
import java.util.List;

import com.patech.imexport.opml.OpmlParser;
import com.java.rssfeed.model.feed.Outline;

public class ApplicationTests {

    private String resourceDir = "resources";
    private final String SEPARATOR = "/";

    @Test
	public void addition_isCorrect() throws Exception {
		assertEquals(4, 2 + 2);
	}

    @Test
    public void testOpmlImport() throws Exception {
        String dirPath = getResourceDir();
        String fileName = "example.opml";
        File f = new File(dirPath + SEPARATOR + fileName);
        String path;
        if (!f.exists()) {
            System.out.println(f.getAbsoluteFile());
            path = f.getAbsolutePath();
        }
        List<Outline> outlines = OpmlParser.read(f);
        assert (outlines.size() == 68);
    }

    public String getResourceDir() {
        return resourceDir;
    }
}


