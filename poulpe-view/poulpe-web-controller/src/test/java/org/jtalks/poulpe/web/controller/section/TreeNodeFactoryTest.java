package org.jtalks.poulpe.web.controller.section;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.jtalks.poulpe.model.entity.Branch;
import org.jtalks.poulpe.model.entity.Section;
import org.testng.annotations.Test;

/**
 * The test class for {@link TreeNodeFactory}
 * 
 * @author costa
 * 
 */
public class TreeNodeFactoryTest {

	private static final String SECTION_DESCRIPTION = "Test section description";
	private static final String SECTION_NAME = "Test section name";
	private static final String BRANCH_DESCRIPTION = "Test branch description";
	private static final String BRANCH_NAME = "Test branch name";
	private static final String BRANCH_UIID = "Test branch uiid";
	private static final int SECTION_ID = 1;
	private static final int SECTION_POSITION = 1;

	private Section createTestSection() {
		Section section = new Section();
		section.setDescription(SECTION_DESCRIPTION);
		section.setName(SECTION_NAME);
		section.setId(SECTION_ID);
		section.setPosition(SECTION_POSITION);
		return section;
	}

	private Branch createTestBranch() {
		Branch branch = new Branch();
		branch.setDeleted(false);
		branch.setDescription(BRANCH_DESCRIPTION);
		branch.setName(BRANCH_NAME);
		branch.setUuid(BRANCH_UIID);
		return branch;
	}

	private Section createTestSectionWithBranches() {
		Section section = createTestSection();
		section.setBranches(new ArrayList<Branch>());
		section.getBranches().add(createTestBranch());
		section.getBranches().add(createTestBranch());
		section.getBranches().add(createTestBranch());
		return section;
	}

	/**
	 * Test single suitable entities
	 */
	@Test
	public void getTreeNodeTest() {
		Section sectionWithOutChildren = createTestSection();
		ExtendedTreeNode testNode = TreeNodeFactory
				.getTreeNode(sectionWithOutChildren);

		assertEquals(testNode.getData(), sectionWithOutChildren);
		assertEquals(testNode.getChildren(), null);
		assertEquals(testNode.isExpanded(), true);

		Branch testBranch = createTestBranch();
		testNode = TreeNodeFactory.getTreeNode(testBranch);

		assertEquals(testNode.getData(), testBranch);
		assertEquals(testNode.getChildren(),null);
		assertEquals(testNode.isExpanded(), true);
		assertEquals(testNode.isLeaf(), true);
	}

	/**
	 * Test suitable entities with relations
	 */
	@Test
	public void getTreeNodeWithRelationsTest() {
		Section sectionWithBranchs = createTestSectionWithBranches();
		ExtendedTreeNode testNode = TreeNodeFactory
				.getTreeNode(sectionWithBranchs);

		assertEquals(testNode.getData(), sectionWithBranchs);
		assertEquals(testNode.getChildren().size(), 3);
		assertEquals(testNode.isExpanded(), true);
		assertNotNull(testNode.getChildAt(0));
		assertTrue(testNode.getChildAt(0).getData() instanceof Branch);
		assertEquals(testNode.getChildAt(0).getData(), sectionWithBranchs
				.getBranches().get(0));
	}

	@Test
	public void getTreeNodes() {
		List<Section> sections = new ArrayList<Section>();
		sections.add(createTestSectionWithBranches());
		sections.add(createTestSectionWithBranches());
		sections.add(createTestSectionWithBranches());
		sections.add(createTestSectionWithBranches());

		List<ExtendedTreeNode> nodes = TreeNodeFactory.getTreeNodes(sections);
		assertEquals(nodes.size(), sections.size());
		for (ExtendedTreeNode node : nodes) {
			assertTrue(node.getData() instanceof Section);
			assertEquals(node.getChildCount(), ((Section) node.getData())
					.getBranches().size());
			for (Object obj : node.getChildren()) {
				assertTrue(obj instanceof ExtendedTreeNode);
				ExtendedTreeNode subnode = (ExtendedTreeNode) obj;
				assertTrue(subnode.getData() instanceof Branch);
				assertTrue(subnode.isLeaf());
			}
		}

	}

}