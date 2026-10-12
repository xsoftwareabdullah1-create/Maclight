package com.winlite10

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkspaceNameTest {
    private fun isSafeName(name: String): Boolean =
        name.isNotBlank() && name != "." && name != ".." &&
            !name.contains("/") && !name.contains("\\") && !name.contains('\u0000')

    @Test fun rejectsBlankNames() {
        assertFalse(isSafeName(""))
        assertFalse(isSafeName("   "))
    }

    @Test fun rejectsPathTraversalNames() {
        assertFalse(isSafeName("../outside"))
        assertFalse(isSafeName("folder/file"))
        assertFalse(isSafeName("folder\\file"))
        assertFalse(isSafeName(".."))
    }

    @Test fun acceptsOrdinaryFolderNames() {
        assertTrue(isSafeName("Projects"))
        assertTrue(isSafeName("my code 2026"))
    }
}
