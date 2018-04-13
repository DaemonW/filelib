package com.grt.filemanager.model;

import android.text.TextUtils;

import com.grt.filemanager.util.FileUtils;

import java.util.Stack;

public class PathStack {

    private Stack<StackData> mStack;

    public PathStack() {
        mStack = new Stack<>();
    }

    public void push(String path, int position) {
        mStack.push(new StackData(path, position));
    }

    /**
     * 出栈并获得栈顶元素中path.
     *
     * @return 栈顶元素中path
     */
    public String popAndPeekPath() {
        if (!mStack.isEmpty()) {
            mStack.pop();
            if (!mStack.isEmpty()) {
                return mStack.peek().getPath();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setPeekPosition(int position) {
        if (!mStack.isEmpty()) {
            StackData data = mStack.peek();

            data.setPosition(position);
        }
    }

    public String peekPath() {
        if (!mStack.isEmpty()) {
            return mStack.peek().getPath();
        } else {
            return "";
        }

    }

    public int peekPosition() {
        if (!mStack.isEmpty()) {
            return mStack.peek().getPosition();
        } else {
            return 0;
        }
    }

    /**
     * 获得当前路径的stack.
     *
     * @return 当前路径的stack
     */
    public Stack<String> getPathStack() {
        Stack<String> stringStack = new Stack<>();

        for (PathStack.StackData path : mStack) {
            stringStack.push(path.getPath());
        }

        return stringStack;
    }

    /**
     * 根据指定结构的String恢复stack内容.
     *
     * @param result 恢复stack内容的String
     */
    public void restorePathStack(String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        }

        String[] array = result.split(",");
        int count = array.length;
        if (count < 1) {
            return;
        }

        if (mStack == null) {
            mStack = new Stack<>();
        } else {
            mStack.clear();
        }

        for (String anArray : array) {
            String[] item = anArray.split(":");
            if (item.length < 2) {
                continue;
            }

            mStack.push(new StackData(item[0], Integer.parseInt(item[1])));
        }
    }

    /**
     * 根据指定path退栈至指定位置，并获得栈顶元素中path.
     *
     * @param path 指定path
     * @return 栈顶元素中path
     */
    public String popAndPeekPath(String path) {
        if (mStack.isEmpty() || TextUtils.isEmpty(path)) {
            return null;
        }

        String currentPath;
        while (!mStack.isEmpty()) {
            currentPath = mStack.peek().getPath();
            if (currentPath.equals(path)) {
                return currentPath;
            }

            mStack.pop();
        }

        return null;
    }

    public void clear() {
        if (mStack != null && !mStack.isEmpty()) {
            mStack.clear();
        }
    }

    @Override
    public String toString() {
        String result = "";

        for (PathStack.StackData path : mStack) {
            result = result.concat(path.toString()).concat(",");
        }

        return result;
    }

    private class StackData {

        private String mPath;

        private int mPosition;

        StackData(String path, int position) {
            this.mPath = path;
            this.mPosition = position;
        }

        public String getPath() {
            return mPath;
        }

        public void setPath(String path) {
            this.mPath = path;
        }

        public int getPosition() {
            return mPosition;
        }

        public void setPosition(int position) {
            this.mPosition = position;
        }

        @Override
        public String toString() {
            return mPath + ":" + mPosition;
        }
    }

    public static String buildStackString(String rootPath, String path) {
        if (TextUtils.isEmpty(rootPath) || TextUtils.isEmpty(path)) {
            return null;
        }

        String stackString = rootPath.concat(":0");

        if (rootPath.equals(path)) {
            return stackString;
        }

        String tmpPath = rootPath.equals("/") ? path.substring(1, path.length()) :
                path.replace(rootPath.concat("/"), "");
        String[] names = tmpPath.split("/");
        String curPath = rootPath;
        for (String name : names) {
            curPath = FileUtils.concatPath(curPath).concat(name);
            stackString += "," + curPath + ":0";
        }

        return stackString;
    }

}
