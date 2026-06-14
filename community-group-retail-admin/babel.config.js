module.exports = {
    'presets': [
        // 直接使用绝对解析结果，绕开 Jest 环境下对预设名的二次解析问题。
        require.resolve('@vue/babel-preset-app')
    ]
};
