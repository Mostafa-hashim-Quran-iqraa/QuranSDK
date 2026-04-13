package com.blacksmith.quranApp.data.util.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import com.blacksmith.quranApp.presentation.base.theme.transparent
import com.blacksmith.quranlib.data.util.helper.isNumber
import com.blacksmith.quranlib.data.util.helper.toDP
import com.blacksmith.quranlib.data.util.helper.toSP

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    hint: String? = "",
    hintColor: Color,
    placeholder: String? = "",
    onValueChange: (String) -> Unit = {},
    padding: Int,
    roundCorner: Int,
    errorMessage: String?,
    text: String = "",
    textSize: Int,
    errorTextSize: Int,
    textColor: Color,
    backgroundColor: Color,
    errorTextColor: Color,
    errorBackgroundColor: Color,
    errorColorBorder: Color,
    enabled: Boolean = true,
    hasError: Boolean = true,
    focusedIndicatorColor: Color = transparent,
    unFocusedIndicatorColor: Color = transparent,
    borderWidth: Int,
    unFocusColorBorder: Color,
    focusColorBorder: Color,
    textFontFamily: FontFamily,
    isPassword: Boolean = false,
    isEmail: Boolean = false,
    isText: Boolean = false,
    isPhone: Boolean = false,
    isNumber: Boolean = false,
    isDone: Boolean = false,
    isSend: Boolean = false,
    isSearch: Boolean = false,
    onAction: KeyboardActionScope.() -> Unit = {},
    isVisible: Boolean = false,
    isLTRDirectionOnly: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    errorIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    maxLines: Int = 1,
    minLines: Int = 1,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        TextField(
            modifier = modifier
                .fillMaxWidth()
                .imePadding()
                .clip(RoundedCornerShape(roundCorner.toDP))
                .border(
                    width = borderWidth.toDP,
                    shape = RoundedCornerShape(roundCorner.toDP),
                    color = if (!errorMessage.isNullOrEmpty()) {
                        errorColorBorder
                    } else {
                        if (isFocused) focusColorBorder
                        else unFocusColorBorder

                    }
                )
                .fillMaxWidth(),
            label = null,
            placeholder = {
                if (!placeholder.isNullOrEmpty())
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = placeholder,
                        maxLines = maxLines,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            color = hintColor,
                            fontSize = textSize.toSP,
                            fontFamily = textFontFamily,
                            textDirection = if (isLTRDirectionOnly)
                                TextDirection.Ltr else TextDirection.Content,
                        ),
                    )
            },
            value = if (isPhone || isNumber) {
                if (isNumber(text)) text else ""
            } else text,
            onValueChange = {
                if (isPhone || isNumber) {
                    if (isNumber(it)) onValueChange(it)
                } else
                    onValueChange(it)
            },
            textStyle = TextStyle(
                color = textColor,
                fontSize = textSize.toSP,
                lineHeight = textSize.toSP,
                fontFamily = textFontFamily,
                textDirection = if (isLTRDirectionOnly)
                    TextDirection.Ltr else TextDirection.Content,
            ),
            minLines = minLines,
            maxLines = maxLines,
            singleLine = singleLine,
            interactionSource = interactionSource,
            visualTransformation = if (isPassword) {
                if (isVisible) VisualTransformation.None
                else PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password
                else if (isEmail) KeyboardType.Email
                else if (isText) KeyboardType.Text
                else if (isPhone) KeyboardType.Phone
                else if (isNumber) KeyboardType.Number
                else KeyboardType.Text,
                imeAction =
                    if (isDone) ImeAction.Done
                    else if (isSearch) ImeAction.Search
                    else if (isSend) ImeAction.Send
                    else if (maxLines > 1) ImeAction.Default
                    else ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onDone = onAction,
                onSearch = onAction
            ),
            enabled = enabled,
            readOnly = !enabled,
            colors = TextFieldDefaults.colors(
                disabledContainerColor = backgroundColor,
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                errorContainerColor = errorBackgroundColor,
                focusedIndicatorColor = focusedIndicatorColor,
                unfocusedIndicatorColor = unFocusedIndicatorColor
            ),
            leadingIcon = leadingIcon, //Start
            trailingIcon = trailingIcon, //End
            prefix = prefix, //Start
            suffix = suffix, //End
        )
        AnimatedVisibility(visible = hasError) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                errorIcon?.let {
                    Box(
                        modifier = Modifier
                            .padding(end = 4.toDP),
                        contentAlignment = Alignment.Center
                    ) {
                        it()
                    }
                }
                Text(
                    text = errorMessage ?: "",
                    color = errorTextColor,
                    lineHeight = 15.toSP,
                    style = TextStyle(
                        color = errorTextColor,
                        fontSize = errorTextSize.toSP,
                        fontFamily = textFontFamily,
                        textDirection = TextDirection.Content,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = padding.toDP)
                )
            }
        }
    }
}

@Composable
fun CustomTextFieldNoVerticalPadding(
    modifier: Modifier = Modifier,
    text: String = "",
    onValueChange: (String) -> Unit = {},
    hintColor: Color = Color.Gray,
    textColor: Color = Color.Black,
    textSize: Int = 16,
    textFontFamily: FontFamily = FontFamily.Default,
    roundCorner: Int = 8,
    borderWidth: Int = 1,
    focusColorBorder: Color = Color.Blue,
    unFocusColorBorder: Color = Color.Gray,
    errorColorBorder: Color = Color.Red,
    backgroundColor: Color = Color.Transparent,
    errorBackgroundColor: Color = Color.Transparent,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    minLines: Int = 1,
    placeholder: String? = "",
    errorMessage: String? = null,
    errorTextColor: Color = Color.Red,
    errorTextSize: Int = 14,
    errorIcon: @Composable (() -> Unit)? = null,
    paddingVertical: Int = 8,
    paddingHorizontal: Int = 8,
    errorPaddingHorizontal: Int = 8,
    paddingIcon: Int = 8,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false,
    isVisible: Boolean = false,
    isEmail: Boolean = false,
    isText: Boolean = false,
    isPhone: Boolean = false,
    isNumber: Boolean = false,
    isDone: Boolean = false,
    isSend: Boolean = false,
    isSearch: Boolean = false,
    onAction: KeyboardActionScope.() -> Unit = {},
    isLTRDirectionOnly: Boolean = false,
    hasError: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(roundCorner.toDP))
                .border(
                    width = borderWidth.toDP,
                    color = if (!errorMessage.isNullOrEmpty()) errorColorBorder
                    else if (isFocused) focusColorBorder else unFocusColorBorder,
                    shape = RoundedCornerShape(roundCorner.toDP)
                )
                .background(backgroundColor)
                .padding(
                    horizontal = paddingHorizontal.toDP,
                    vertical = paddingVertical.toDP
                ) // padding أفقي فقط
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                leadingIcon?.invoke()
                prefix?.invoke()

                Box(modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = paddingIcon.toDP)) {
                    BasicTextField(
                        modifier = modifier
                            .fillMaxWidth()
                            .imePadding(),
                        value = if ((isPhone || isNumber) && !text.all { it.isDigit() }) "" else text,
                        onValueChange = {
                            if (isPhone || isNumber) {
                                if (it.all { char -> char.isDigit() }) onValueChange(it)
                            } else onValueChange(it)
                        },
                        singleLine = singleLine,
                        textStyle = TextStyle(
                            color = textColor,
                            fontSize = textSize.toSP,
                            fontFamily = textFontFamily,
                            lineHeight = textSize.toSP,
                            textDirection = if (isLTRDirectionOnly) TextDirection.Ltr else TextDirection.Content
                        ),
                        enabled = enabled,
                        interactionSource = interactionSource,
                        visualTransformation = if (isPassword) {
                            if (isVisible) VisualTransformation.None
                            else PasswordVisualTransformation()
                        } else VisualTransformation.None,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = when {
                                isPassword -> KeyboardType.Password
                                isEmail -> KeyboardType.Email
                                isText -> KeyboardType.Text
                                isPhone -> KeyboardType.Phone
                                isNumber -> KeyboardType.Number
                                else -> KeyboardType.Text
                            },
                            imeAction = when {
                                isDone -> ImeAction.Done
                                isSearch -> ImeAction.Search
                                isSend -> ImeAction.Send
                                maxLines > 1 -> ImeAction.Default
                                else -> ImeAction.Next
                            }
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = onAction,
                            onSearch = onAction,
                            onSend = onAction,
                            onNext = onAction
                        ),
                        cursorBrush = SolidColor(textColor)
                    )

                    // Placeholder
                    if (text.isEmpty() && !placeholder.isNullOrEmpty()) {
                        Text(
                            text = placeholder,
                            style = TextStyle(
                                color = hintColor,
                                fontSize = textSize.toSP,
                                fontFamily = textFontFamily,
                                lineHeight = textSize.toSP,
                                textDirection = if (isLTRDirectionOnly) TextDirection.Ltr else TextDirection.Content
                            )
                        )
                    }
                }

                suffix?.invoke()
                trailingIcon?.invoke()
            }
        }

        // Error message
        AnimatedVisibility(visible = hasError && !errorMessage.isNullOrEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = errorPaddingHorizontal.toDP, vertical = 2.toDP)
            ) {
                errorIcon?.invoke()
                Text(
                    text = errorMessage ?: "",
                    color = errorTextColor,
                    fontSize = errorTextSize.toSP, // استخدمنا هنا errorTextSize
                    fontFamily = textFontFamily
                )
            }
        }
    }
}
