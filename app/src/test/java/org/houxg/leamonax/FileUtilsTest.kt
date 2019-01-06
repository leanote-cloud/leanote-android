package org.houxg.leamonax

import junit.framework.Assert
import org.houxg.leamonax.utils.FileUtils
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.io.File

class FileUtilsTest {

    @Before
    fun setUp() {

    }

    @Test
    fun testIsImageFile() {
        Assert.assertEquals(true, FileUtils.isImageFile(getMockFile("a.jpeg", true)))
        Assert.assertEquals(true, FileUtils.isImageFile(getMockFile("a.jpg", true)))
        Assert.assertEquals(true, FileUtils.isImageFile(getMockFile("a.png", true)))
        Assert.assertEquals(true, FileUtils.isImageFile(getMockFile("a.gif", true)))

        Assert.assertEquals(true, FileUtils.isImageFile(getMockFile("a.JPEG", true)))
        Assert.assertEquals(true, FileUtils.isImageFile(getMockFile("a.JPG", true)))
        Assert.assertEquals(true, FileUtils.isImageFile(getMockFile("a.PNG", true)))
        Assert.assertEquals(true, FileUtils.isImageFile(getMockFile("a.GIF", true)))
    }

    private fun getMockFile(name: String, isFile: Boolean): File {
        val mock = mock(File::class.java)
        `when`(mock.isFile).thenReturn(isFile)
        `when`(mock.name).thenReturn(name)
        return mock
    }
}
