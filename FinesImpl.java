// Lachlan McDonald
//For problem details see video
import java.util.*;

/**
 * An implementation of AVL Insertion
 * Loosely based off the AVL Tree implentation at W3Schools: 
 */
public class FinesImpl  {

    public long countFines(int[] priorities) {
      AVLTree tree = new AVLTree();
      return tree.finecount(priorities);
    }
  
    /** 
    * A class to generate nodes for the AVL tree 
    */
    public static class AVLNode {
      int priority; // node priority (or key)
      AVLNode left; // contains left subtree
      AVLNode right; // contains left subtree
      int height; // contains height of node - updated during balancing
      int size; // contains number of nodes in tree rooted by this node
      // int size is updated during balancing, and is used to determine how many
      // prioritys an inserted node is bigger than
  
      /**
       * Constructor for AVL Tree nodes
       * Initialised with a height of 1 and size of 1
       * @param key is the priority value of the node to be implemented
       */
      public AVLNode(int key) {
        priority = key;
        height = 1;
        size = 1;
        left = null;
        right = null;
      }
    }
  
    /**
     * Class for the implementation of an AVLTree with size parameter to track number of nodes in subtrees
     */
    public static class AVLTree {
  
      // Instance variables
      AVLNode root;
      long total_fines = 0;
  
      /**
       * Constructor for the AVL Tree
       */
      public AVLTree() {
        root = null;
      }
  
      /**
       * Helper method
       * @param node checked node
       * @return height of checked node
       */
      public int height(AVLNode node) {
        if (node == null)
          return 0;
        else {
          return node.height;
        }
      }
  
      /**
       * @param node the node checked
       * @return size of subtree of node
       */
      public int size(AVLNode node) {
        if (node == null)
          return 0;
        else {
          return node.size;
        }
      }
  
      /**
       * @param node - node checked
       * @return the difference in heights between the two AVLNodes
       * Returns a positive integer returned if left is taller and negative integer in right is (0 if balanced)
       */
      public int checkHeightBalance(AVLNode node) {
        return height(node.left) - height(node.right);
      }
  
  
      /**
       * Mutator method to update height of a node
       * @param node node whose height is calculated
       */
      public void updateHeight(AVLNode node) {
        if (height(node.left) >= height(node.right)) {
          node.height = height(node.left) + 1;
        } else {
          node.height = height(node.right) + 1;
        }
      }
  
      /**
       * To insert an element, we:
       * 1. Insert the element into an external node (as per usual for a binary search
       * tree).
       * 2. Starting with its parent, check each ancestor of the new node to ensure
       * the left
       * and right subtree heights differ by less than two.
       * 3. If we find a node such that one child has height k − 2 and the other has
       * height
       * k, then we perform a rotation to restore balance.
       */
  
      /**
       * Inserts a node into the AVL tree and initiates required rotations for balancing
       * @param node the root node of the tree/subtree
       * @param priority the priority of the element to be inserted
       * @param size the size of the element to be inserted
       * @return the new root node of the tree
       */
      AVLNode insert(AVLNode node, int priority, int size) {
        // If at end of tree branch, add a node
        if (node == null) {
          return new AVLNode(priority);
        }
        // Traverse binary tree to get to end of branch
        // Note: since a fan with the same priority as one after it does not have to
        // pay a fine, nodes with same priority go to left
        // priority and size parameters for inserted node are inserted into left child
        // node (recursive)
        if (priority <= node.priority) {
          node.left = insert(node.left, priority, size);
        } else {
          // Add to total_fines the number of elements in the AVLTree that have a smaller
          // priority than the inserted element
          total_fines += (size(node.left) + 1);
          node.right = insert(node.right, priority, size);
        }
  
        // Update the node's priority and size variables
        updateHeight(node);
        node.size = size(node.left) + size(node.right) + 1;
        // Check if rotation is required, and execute the rotation
        return rotate(node);
      }
  
      /**
       * Determines and executes rotations based on state of balance of the tree from the input node
       * @param node the node from which to calculate balance
       * @return the root node of the subtree after balancing
       */
      public AVLNode rotate(AVLNode node) {
        if (checkHeightBalance(node) > 1) {
          if (checkHeightBalance(node.left) > 0) {
            // Right rotate
            node = rightRotate(node);
          } else {
            // Left Right rotate
            node.left = leftRotate(node.left);
            node = rightRotate(node);
          }
        } else if (checkHeightBalance(node) < -1) {
          if (checkHeightBalance(node.right) > 0) {
            // Right Left rotate
            node.right = rightRotate(node.right);
            node = leftRotate(node);
          } else {
            // Left rotate
            node = leftRotate(node);
          }
        }
        return node;
      }
  
      /**
       * Performs a right rotate on a node
       * @param node the node from which the rotation is based
       * @return the new root node of the rotated subtree
       */
      public AVLNode rightRotate(AVLNode node) {
        AVLNode left_child = node.left;
        node.left = left_child.right;
        left_child.right = node;
        // Update height and size. NOTE: the only nodes whose heights and sizes may
        // change are those whose child nodes have changed
        updateHeight(node);
        updateHeight(left_child);
        node.size = size(node.left) + size(node.right) + 1;
        left_child.size = size(left_child.left) + size(left_child.right) + 1;
        // return root node
        return left_child;
      }
      
      /**
       * Performs a left rotate on a node
       * @param node the node from which the rotation is based
       * @return the new root node of the rotated subtree
       */
      public AVLNode leftRotate(AVLNode node) {
        AVLNode right_child = node.right;
        node.right = right_child.left;
        right_child.left = node;
        // Update height and size. NOTE: the only nodes whose heights and sizes may
        // change are those whose child nodes have changed
        updateHeight(node);
        updateHeight(right_child);
        node.size = size(node.left) + size(node.right) + 1;
        right_child.size = size(right_child.left) + size(right_child.right) + 1;
        // return root node
        return right_child;
      }
  
      /**
       * 
       * @param p_list the list of priorities of elements to be entered in the tree
       * @return total number of fines (size of elements to left of inserted element accumulated for each inserted node)
       */
      public long finecount(int[] p_list) {
        for (int i = 0; i < p_list.length; i++) {
          // System.out.println("Inserting: "+p_list[i]);
          root = insert(root, p_list[i], 1);
        }
        //$100 per fine
        return total_fines*100;
      }
  
    }

    //TEST CASE
    public static void main(String[] args) {
    int[] fines = {29, 29, 7, 19, 17, 13, 11, 5, 3, 2};
    AVLTree testTreeFines = new AVLTree();
    System.out.println("Test Case fines: "+testTreeFines.finecount(fines));

    int[] nofines = {13, 8, 8, 5, 3, 2, 0};
    AVLTree testTreeNoFines = new AVLTree();
    System.out.println("Test Case nofines: "+testTreeNoFines.finecount(nofines));
    }
  }
