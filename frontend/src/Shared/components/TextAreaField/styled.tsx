import styled from 'styled-components';

export const Box = styled.div``;

export const Label = styled.label`
  display: block;
  font-size: 1.4rem;
  font-weight: 600;

  margin-bottom: 0.7rem;
`;

type TextAreaProps = {
  isError: boolean;
};

export const TextArea = styled.textarea<TextAreaProps>`
  width: 100%;
  min-height: 70px;
  border-radius: 10px;
  padding: 1rem;
  font-size: 1.4rem;
  resize: none;
  outline: none;

  border: 1px solid
    ${({ isError, theme }) => (isError ? theme.colors.pink_200 : theme.colors.gray_150)};
`;

export const DescriptionBox = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 0.5rem;

  font-size: 1.4rem;
`;
