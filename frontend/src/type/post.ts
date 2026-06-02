export type PostDto = {
    id: number;
    title: string;
    createDate: string;
    modifyDate: string;
}

export type PostWithContentDto = 
    PostDto & { content: string; }

export type PostCommentDto = {
    id: number;
    content: string;
    createDate: string;
    modifyDate: string;
}